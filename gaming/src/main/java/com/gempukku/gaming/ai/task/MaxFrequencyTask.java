package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.EntityRefReference;
import com.gempukku.gaming.ai.builder.TaskBuilder;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;

import java.util.Map;

public class MaxFrequencyTask extends AbstractWrapperAITask<EntityRefReference> {
    @Inject
    private TimeManager timeManager;

    private long frequency;

    public MaxFrequencyTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        frequency = ((Number) taskData.get("frequency")).longValue();
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        long time = timeManager.getTime();
        Long lastExecution = reference.getValue(getId(), "lastExecution", Long.class);
        if (lastExecution == null || lastExecution + frequency < time) {
            reference.setValue(getId(), "lastExecution", time);
            return getTask().startTask(reference);
        }
        return AITaskResult.FAILURE;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        return getTask().continueTask(reference);
    }

    @Override
    public void cancelTask(EntityRefReference reference) {
        getTask().cancelTask(reference);
    }
}
