package com.gempukku.gaming.ai;

import java.util.Collection;

public interface AITask<Reference extends AIReference> {
    AITaskResult startTask(Reference reference);

    AITaskResult continueTask(Reference reference);

    void cancelTask(Reference reference);

    AITask getParent();

    Collection<AITask<Reference>> getRunningTasks(Reference reference);
}
