package com.gempukku.gaming.ai;

import java.util.Collection;

public interface AITask {
    AITaskResult startTask(AIReference reference);

    AITaskResult continueTask(AIReference reference);

    void cancelTask(AIReference reference);

    AITask getParent();

    Collection<AITask> getRunningTasks(AIReference reference);
}
