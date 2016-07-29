package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Map;

public class RepeatTask<Reference extends AIReference> extends AbstractWrapperAITask<Reference> {
    public RepeatTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        return execute(reference);
    }

    private AITaskResult execute(Reference reference) {
        while (true) {
            AITaskResult result = getTask().startTask(reference);
            if (result == AITaskResult.FAILURE) {
                return result;
            } else if (result == AITaskResult.RUNNING) {
                return result;
            }
        }
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        AITaskResult result = getTask().continueTask(reference);
        if (result == AITaskResult.FAILURE) {
            return result;
        } else if (result == AITaskResult.RUNNING) {
            return result;
        }
        return execute(reference);
    }

    @Override
    public void cancelTask(Reference reference) {
        getTask().cancelTask(reference);
    }
}
