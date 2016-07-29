package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Map;

public class RepeatTimesTask<Reference extends AIReference> extends AbstractAITask<Reference> {
    private int times;
    private AITask<Reference> task;

    public RepeatTimesTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);

        times = ((Number) taskData.get("times")).intValue();
        task = taskBuilder.buildTask(this, (Map<String, Object>) taskData.get("task"));
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        int start = 0;
        return executeFrom(reference, start);
    }

    private AITaskResult executeFrom(Reference reference, int start) {
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
    public AITaskResult continueTask(Reference reference) {
        int start = reference.getValue(getId(), "i", Integer.class);
        AITaskResult result = task.continueTask(reference);
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
    public void cancelTask(Reference reference) {
        task.cancelTask(reference);
    }
}
