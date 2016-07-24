package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Map;

public class RepeatTimesTask extends AbstractAITask {
    private int times;
    private AITask task;

    public RepeatTimesTask(String id, AITask parent, TaskBuilder taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);

        times = ((Number) taskData.get("times")).intValue();
        task = taskBuilder.buildTask(this, (Map<String, Object>) taskData.get("task"));
    }

    @Override
    public AITaskResult startTask(AIReference reference) {
        int start = 0;
        return executeFrom(reference, start);
    }

    private AITaskResult executeFrom(AIReference reference, int start) {
        for (int i = start; i < times; i++) {
            AITaskResult result = task.startTask(reference);
            if (result == AITaskResult.FAILURE) {
                return result;
            } else if (result == AITaskResult.RUNNING) {
                reference.setValue(getId(), "i", i);
                return result;
            }
        }
        return AITaskResult.SUCCESS;
    }

    @Override
    public AITaskResult continueTask(AIReference reference) {
        int start = reference.getValue(getId(), "i", Integer.class);
        AITaskResult result = continueTask(reference);
        if (result == AITaskResult.FAILURE) {
            reference.removeValue(getId(), "i");
            return result;
        } else if (result == AITaskResult.RUNNING) {
            return result;
        }
        AITaskResult afterResult = executeFrom(reference, start + 1);
        if (afterResult != AITaskResult.SUCCESS) {
            return afterResult;
        }
        reference.removeValue(getId(), "i");
        return AITaskResult.SUCCESS;
    }

    @Override
    public void cancelTask(AIReference reference) {
        task.cancelTask(reference);
    }
}
