package com.gempukku.gaming.ai.builder;

import com.gempukku.gaming.ai.AITask;

import java.util.Map;

public interface TaskBuilder {
    AITask loadBehavior(AITask parent, String behaviorName);

    AITask buildTask(AITask parent, Map<String, Object> behaviorData);
}
