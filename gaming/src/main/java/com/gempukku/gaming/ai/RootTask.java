package com.gempukku.gaming.ai;

import java.util.Collection;

public class RootTask<Reference extends AIReference> {
    private String id;
    private AITask<Reference> task;

    public RootTask(String id, AITask<Reference> task) {
        this.id = id;
        this.task = task;
    }

    public void processAI(Reference reference) {
        Boolean started = reference.getValue(id, "started", Boolean.class);
        if (started == null) {
            AITaskResult result = task.startTask(reference);
            if (result == AITaskResult.RUNNING) {
                reference.setValue(id, "started", true);
            }
        } else {
            AITaskResult result = task.continueTask(reference);
            if (result != AITaskResult.RUNNING) {
                reference.removeValue(id, "started");
            }
        }
    }

    public Collection<AITask<Reference>> getRunningTasks(Reference reference) {
        return task.getRunningTasks(reference);
    }
}
