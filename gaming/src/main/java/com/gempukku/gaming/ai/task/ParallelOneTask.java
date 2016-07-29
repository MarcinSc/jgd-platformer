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

public class ParallelOneTask<Reference extends AIReference> extends AbstractAITask<Reference> {
    private List<AITask<Reference>> tasks;

    public ParallelOneTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);

        tasks = TaskCollectionUtil.<Reference>buildTasks(this, taskBuilder, (List<Map<String, Object>>) taskData.get("tasks"));
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        for (int i = 0; i < tasks.size(); i++) {
            AITaskResult result = tasks.get(i).startTask(reference);
            if (result != AITaskResult.RUNNING) {
                cancelAllBefore(i, reference);
                return result;
            }
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        for (int i = 0; i < tasks.size(); i++) {
            AITaskResult result = tasks.get(i).continueTask(reference);
            if (result != AITaskResult.RUNNING) {
                cancelAllBefore(i, reference);
                cancelAllAfter(i, reference);
                return result;
            }
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public void cancelTask(Reference reference) {
        cancelAllBefore(tasks.size(), reference);
    }

    private void cancelAllBefore(int index, Reference reference) {
        for (int i = 0; i < index; i++)
            tasks.get(i).cancelTask(reference);
    }

    private void cancelAllAfter(int index, Reference reference) {
        for (int i = index + 1; i < tasks.size(); i++)
            tasks.get(i).cancelTask(reference);
    }

    @Override
    public Collection<AITask<Reference>> getRunningTasks(Reference reference) {
        Set<AITask<Reference>> result = new HashSet<>();
        for (int i = 0; i < tasks.size(); i++) {
            result.addAll(tasks.get(i).getRunningTasks(reference));
        }
        return result;
    }
}
