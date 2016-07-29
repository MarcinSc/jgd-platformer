package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParallelAllTask<Reference extends AIReference> extends AbstractAITask<Reference> {
    public static final String FINISHED_KEY = "finished";
    private List<AITask<Reference>> tasks;

    public ParallelAllTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);

        tasks = TaskCollectionUtil.<Reference>buildTasks(this, taskBuilder, (List<Map<String, Object>>) taskData.get("tasks"));
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        boolean[] finished = new boolean[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            AITaskResult result = tasks.get(i).startTask(reference);
            if (result == AITaskResult.FAILURE) {
                cancelNotFinishedBefore(reference, i, finished);
                return result;
            } else if (result == AITaskResult.SUCCESS) {
                finished[i] = true;
            }
        }
        if (isAllFinished(finished)) {
            return AITaskResult.SUCCESS;
        } else {
            storeFinished(reference, finished);
            return AITaskResult.RUNNING;
        }
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        boolean[] finished = getFinished(reference);
        for (int i = 0; i < tasks.size(); i++) {
            if (!finished[i]) {
                AITaskResult result = tasks.get(i).continueTask(reference);
                if (result == AITaskResult.FAILURE) {
                    cancelNotFinishedBefore(reference, i, finished);
                    clean(reference);
                    return result;
                } else if (result == AITaskResult.SUCCESS) {
                    finished[i] = true;
                }
            }
        }
        if (isAllFinished(finished)) {
            clean(reference);
            return AITaskResult.SUCCESS;
        } else {
            storeFinished(reference, finished);
            return AITaskResult.RUNNING;
        }
    }

    private boolean isAllFinished(boolean[] finished) {
        for (int i = 0; i < finished.length; i++) {
            if (!finished[i])
                return false;
        }
        return true;
    }

    @Override
    public void cancelTask(Reference reference) {
        boolean[] finished = getFinished(reference);
        cancelNotFinishedBefore(reference, tasks.size(), finished);
    }

    private void cancelNotFinishedBefore(Reference reference, int size, boolean[] finished) {
        for (int i = 0; i < size; i++) {
            if (!finished[i])
                tasks.get(i).cancelTask(reference);
        }
    }

    private void storeFinished(Reference reference, boolean[] finished) {
        reference.setValue(getId(), FINISHED_KEY, finished);
    }

    private boolean[] getFinished(Reference reference) {
        return reference.getValue(getId(), FINISHED_KEY, boolean[].class);
    }

    private void clean(Reference reference) {
        reference.removeValue(getId(), FINISHED_KEY);
    }

    @Override
    public Collection<AITask<Reference>> getRunningTasks(Reference reference) {
        Set<AITask<Reference>> result = new HashSet<>();
        boolean[] finished = getFinished(reference);
        for (int i = 0; i < tasks.size(); i++) {
            if (!finished[i]) {
                result.addAll(tasks.get(i).getRunningTasks(reference));
            }
        }
        return result;
    }
}
