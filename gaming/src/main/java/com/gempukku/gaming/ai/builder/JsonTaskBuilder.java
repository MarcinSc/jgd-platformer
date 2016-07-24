package com.gempukku.gaming.ai.builder;

import com.gempukku.gaming.ai.AITask;
import com.gempukku.secsy.context.SystemContext;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class JsonTaskBuilder implements TaskBuilder {
    private int taskId = 0;
    private SystemContext<Object> context;
    private Map<String, JSONObject> behaviorJsons = new HashMap<>();
    private Map<String, Class<? extends AITask>> taskTypes;

    public JsonTaskBuilder(SystemContext<Object> context, Map<String, JSONObject> behaviorJsons, Map<String, Class<? extends AITask>> taskTypes) {
        this.context = context;
        this.behaviorJsons = behaviorJsons;
        this.taskTypes = taskTypes;
    }

    public String getNextId() {
        return String.valueOf(taskId++);
    }

    @Override
    public AITask buildTask(AITask parent, Map<String, Object> behaviorData) {
        String type = (String) behaviorData.get("type");
        Class<? extends AITask> taskClass = taskTypes.get(type);
        Constructor<? extends AITask> constructor = null;
        try {
            constructor = taskClass.getConstructor(String.class, AITask.class, TaskBuilder.class, Map.class);
            String nextId = getNextId();
            AITask task = constructor.newInstance(nextId, parent, this, behaviorData);
            context.initializeObject(task);
            return task;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException exp) {
            throw new RuntimeException("Unable to build task of type - " + type, exp);
        }
    }

    @Override
    public AITask loadBehavior(AITask parent, String behaviorName) {
        JSONObject behaviorJson = behaviorJsons.get(behaviorName);
        return buildTask(parent, behaviorJson);
    }
}
