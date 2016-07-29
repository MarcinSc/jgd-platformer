package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Map;

public class AttemptTimesTask<Reference extends AIReference> extends AbstractWrapperAITask<Reference> {
    private int times;

    public AttemptTimesTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);

        times = ((Number) taskData.get("times")).intValue();
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        int start = 0;
        return executeFrom(reference, start);
    }

    private AITaskResult executeFrom(Reference reference, int start) {
        for (int i = start; i < times; i++) {
            AITaskResult result = getTask().startTask(reference);
            if (result == AITaskResult.SUCCESS) {
                return result;
            } else if (result == AITaskResult.RUNNING) {
                reference.setValue(getId(), "i", i);
                return result;
            }
        }
        return AITaskResult.FAILURE;
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        int start = reference.getValue(getId(), "i", Integer.class);
        AITaskResult result = getTask().continueTask(reference);
        if (result == AITaskResult.SUCCESS) {
            reference.removeValue(getId(), "i");
            return result;
        } else if (result == AITaskResult.RUNNING) {
            return result;
        }
        AITaskResult afterResult = executeFrom(reference, start + 1);
        if (afterResult != AITaskResult.FAILURE) {
            return afterResult;
        }
        reference.removeValue(getId(), "i");
        return AITaskResult.FAILURE;
    }

    @Override
    public void cancelTask(Reference reference) {
        reference.removeValue(getId(), "i");
        getTask().cancelTask(reference);
    }
}
