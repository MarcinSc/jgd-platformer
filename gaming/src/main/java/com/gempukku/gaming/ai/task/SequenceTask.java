package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SequenceTask extends AbstractAITask {
    private static final String INDEX_KEY = "index";

    private List<AITask> tasks;

    public SequenceTask(String id, AITask parent, TaskBuilder taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);

        tasks = TaskCollectionUtil.buildTasks(this, taskBuilder, (List<Map<String, Object>>) taskData.get("tasks"));
    }

    @Override
    public AITaskResult startTask(AIReference reference) {
        int index = 0;
        return executeTasks(reference, index);
    }

    @Override
    public AITaskResult continueTask(AIReference reference) {
        int index = reference.getValue(getId(), INDEX_KEY, Integer.class);
        AITaskResult result = tasks.get(index).continueTask(reference);
        if (processResult(reference, index, result))
            return result;

        return executeTasks(reference, index + 1);
    }

    @Override
    public void cancelTask(AIReference reference) {
        int index = reference.getValue(getId(), INDEX_KEY, Integer.class);
        tasks.get(index).cancelTask(reference);
        reference.removeValue(getId(), INDEX_KEY);
    }

    @Override
    public Collection<AITask> getRunningTasks(AIReference reference) {
        Integer index = reference.getValue(getId(), INDEX_KEY, Integer.class);
        if (index != null) {
            return tasks.get(index).getRunningTasks(reference);
        }
        return null;
    }

    private AITaskResult executeTasks(AIReference reference, int index) {
        for (int i = index; i < tasks.size(); i++) {
            AITask aiTask = tasks.get(i);
            AITaskResult result = aiTask.startTask(reference);
            if (processResult(reference, i, result))
                return result;
        }
        reference.removeValue(getId(), INDEX_KEY);
        return AITaskResult.SUCCESS;
    }

    private boolean processResult(AIReference reference, int index, AITaskResult result) {
        if (result == AITaskResult.FAILURE) {
            reference.removeValue(getId(), INDEX_KEY);
            return true;
        } else if (result == AITaskResult.RUNNING) {
            reference.setValue(getId(), INDEX_KEY, index);
            return true;
        }
        return false;
    }
}
