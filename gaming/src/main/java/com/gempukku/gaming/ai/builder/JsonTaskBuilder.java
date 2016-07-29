package com.gempukku.gaming.ai.builder;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.secsy.context.SystemContext;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class JsonTaskBuilder<Reference extends AIReference> implements TaskBuilder<Reference> {
    private int taskId = 0;
    private SystemContext context;
    private Map<String, JSONObject> behaviorJsons = new HashMap<>();
    private Map<String, Class<? extends AITask<Reference>>> taskTypes;

    public JsonTaskBuilder(SystemContext context, Map<String, JSONObject> behaviorJsons, Map<String, Class<? extends AITask<Reference>>> taskTypes) {
        this.context = context;
        this.behaviorJsons = behaviorJsons;
        this.taskTypes = taskTypes;
    }

    public String getNextId() {
        return String.valueOf(taskId++);
    }

    @Override
    public AITask<Reference> buildTask(AITask parent, Map<String, Object> behaviorData) {
        String type = (String) behaviorData.get("type");
        Class<? extends AITask<Reference>> taskClass = taskTypes.get(type);
        Constructor<? extends AITask<Reference>> constructor = null;
        try {
            constructor = taskClass.getConstructor(String.class, AITask.class, TaskBuilder.class, Map.class);
            String nextId = getNextId();
            AITask<Reference> task = constructor.newInstance(nextId, parent, this, behaviorData);
            context.initializeObject(task);
            return task;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException exp) {
            throw new RuntimeException("Unable to build task of type - " + type, exp);
        }
    }

    @Override
    public AITask<Reference> loadBehavior(AITask parent, String behaviorName) {
        JSONObject behaviorJson = behaviorJsons.get(behaviorName);
        return buildTask(parent, behaviorJson);
    }
}
