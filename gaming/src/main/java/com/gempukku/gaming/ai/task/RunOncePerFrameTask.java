package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Map;

public class RunOncePerFrameTask<Reference extends AIReference> extends AbstractAITask<Reference> {
    private final AITask<Reference> task;

    public RunOncePerFrameTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        task = taskBuilder.buildTask(this, (Map<String, Object>) taskData.get("task"));
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        return execute(reference);
    }

    private AITaskResult execute(Reference reference) {
        AITaskResult result = task.startTask(reference);
        if (result == AITaskResult.FAILURE) {
            return result;
        } else if (result == AITaskResult.RUNNING) {
            task.cancelTask(reference);
            return AITaskResult.RUNNING;
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        AITaskResult result = task.startTask(reference);
        if (result == AITaskResult.FAILURE) {
            return result;
        } else if (result == AITaskResult.RUNNING) {
            task.cancelTask(reference);
            return AITaskResult.RUNNING;
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public void cancelTask(Reference reference) {
    }
}
