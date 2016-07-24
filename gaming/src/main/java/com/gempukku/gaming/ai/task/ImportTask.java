package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Map;

public class ImportTask extends AbstractAITask {
    private AITask task;

    public ImportTask(String id, AITask parent, TaskBuilder taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        task = taskBuilder.loadBehavior(this, (String) taskData.get("behavior"));
    }

    @Override
    public AITaskResult startTask(AIReference reference) {
        return task.startTask(reference);
    }

    @Override
    public AITaskResult continueTask(AIReference reference) {
        return task.continueTask(reference);
    }

    @Override
    public void cancelTask(AIReference reference) {
        task.cancelTask(reference);
    }
}
