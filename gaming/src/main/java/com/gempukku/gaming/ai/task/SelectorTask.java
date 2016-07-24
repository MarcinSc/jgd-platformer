package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SelectorTask extends AbstractAITask {
    private static final String INDEX_KEY = "index";

    private List<AITask> tasks;

    public SelectorTask(String id, AITask parent, TaskBuilder taskBuilder, Map<String, Object> taskData) {
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

        return executeTasks(reference, index);
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
        while (index < tasks.size()) {
            AITask aiTask = tasks.get(index);
            AITaskResult result = aiTask.startTask(reference);
            if (result == AITaskResult.SUCCESS) {
                reference.removeValue(getId(), INDEX_KEY);
                return result;
            } else if (result == AITaskResult.RUNNING) {
                reference.setValue(getId(), INDEX_KEY, index);
                return result;
            }
        }
        reference.removeValue(getId(), INDEX_KEY);
        return AITaskResult.FAILURE;
    }
}
