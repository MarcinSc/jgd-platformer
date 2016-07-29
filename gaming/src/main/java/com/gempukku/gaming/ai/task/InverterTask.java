package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Map;

public class InverterTask<Reference extends AIReference> extends AbstractAITask<Reference> {
    private AITask<Reference> task;

    public InverterTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);

        task = taskBuilder.buildTask(this, (Map<String, Object>) taskData.get("task"));
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        return invert(task.startTask(reference));
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        return invert(task.continueTask(reference));
    }

    @Override
    public void cancelTask(Reference reference) {
        task.cancelTask(reference);
    }

    private AITaskResult invert(AITaskResult result) {
        if (result == AITaskResult.FAILURE)
            return AITaskResult.SUCCESS;
        else if (result == AITaskResult.SUCCESS)
            return AITaskResult.FAILURE;
        else
            return AITaskResult.RUNNING;
    }
}
