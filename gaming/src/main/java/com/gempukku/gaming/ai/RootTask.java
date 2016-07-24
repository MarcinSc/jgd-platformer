package com.gempukku.gaming.ai;

public class RootTask {
    private String id;
    private AITask task;

    public RootTask(String id, AITask task) {
        this.id = id;
        this.task = task;
    }

    public void processAI(AIReference reference) {
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
}
