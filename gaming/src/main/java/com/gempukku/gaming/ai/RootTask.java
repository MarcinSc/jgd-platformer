package com.gempukku.gaming.ai;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RootTask<Reference extends AIReference> {
    private static Logger logger = Logger.getLogger("ai");
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
        if (logger.isLoggable(Level.FINE)) {
            Collection<AITask<Reference>> runningTasks = getRunningTasks(reference);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("AI state for " + reference.toString() + "\n");
            for (AITask<Reference> runningTask : runningTasks) {
                stringBuilder.append(runningTask.getClass().getSimpleName());
            }
            logger.fine(stringBuilder.toString());
        }
    }

    public Collection<AITask<Reference>> getRunningTasks(Reference reference) {
        Boolean started = reference.getValue(id, "started", Boolean.class);
        if (started != null)
            return task.getRunningTasks(reference);
        else
            return Collections.emptySet();
    }
}
