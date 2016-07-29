package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Collection;
import java.util.Map;

public class ImportTask<Reference extends AIReference> extends AbstractAITask<Reference> {
    private AITask<Reference> task;

    public ImportTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        task = taskBuilder.loadBehavior(this, (String) taskData.get("behavior"));
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        return task.startTask(reference);
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        return task.continueTask(reference);
    }

    @Override
    public void cancelTask(Reference reference) {
        task.cancelTask(reference);
    }

    @Override
    public Collection<AITask<Reference>> getRunningTasks(Reference reference) {
        return task.getRunningTasks(reference);
    }
}
