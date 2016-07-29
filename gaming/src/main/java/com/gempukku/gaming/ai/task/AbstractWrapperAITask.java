package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractWrapperAITask<Reference extends AIReference> extends AbstractAITask<Reference> {
    private AITask<Reference> task;

    public AbstractWrapperAITask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        task = taskBuilder.buildTask(this, (Map<String, Object>) taskData.get("task"));
    }

    public AITask<Reference> getTask() {
        return task;
    }

    @Override
    public Collection<AITask<Reference>> getRunningTasks(Reference reference) {
        return task.getRunningTasks(reference);
    }
}
