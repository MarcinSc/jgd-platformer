package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class RepeatOncePerFrameTask<Reference extends AIReference> extends AbstractAITask<Reference> {
    private final AITask<Reference> task;

    public RepeatOncePerFrameTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        task = taskBuilder.buildTask(this, (Map<String, Object>) taskData.get("task"));
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        AITaskResult result = task.startTask(reference);
        if (result == AITaskResult.FAILURE) {
            return AITaskResult.FAILURE;
        } else if (result == AITaskResult.RUNNING) {
            reference.setValue(getId(), "running", true);
            return AITaskResult.RUNNING;
        } else {
            return AITaskResult.RUNNING;
        }
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        Boolean running = reference.getValue(getId(), "running", Boolean.class);
        if (running == null) {
            AITaskResult result = task.startTask(reference);
            if (result == AITaskResult.FAILURE) {
                return result;
            } else if (result == AITaskResult.RUNNING) {
                reference.setValue(getId(), "running", true);
                return AITaskResult.RUNNING;
            } else {
                return AITaskResult.RUNNING;
            }
        } else {
            AITaskResult result = task.continueTask(reference);
            if (result == AITaskResult.FAILURE) {
                reference.removeValue(getId(), "running");
                return AITaskResult.FAILURE;
            } else if (result == AITaskResult.RUNNING) {
                return AITaskResult.RUNNING;
            } else {
                reference.removeValue(getId(), "running");
                return AITaskResult.RUNNING;
            }
        }
    }

    @Override
    public void cancelTask(Reference reference) {
        Boolean running = reference.getValue(getId(), "running", Boolean.class);
        if (running != null)
            task.cancelTask(reference);
    }

    @Override
    public Collection<AITask<Reference>> getRunningTasks(Reference reference) {
        Boolean running = reference.getValue(getId(), "running", Boolean.class);
        if (running != null)
            return task.getRunningTasks(reference);
        else
            return Collections.singleton(this);
    }
}
