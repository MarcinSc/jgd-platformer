package com.gempukku.gaming.ai.map;

import com.gempukku.gaming.ai.AIReference;

import java.util.Map;

public abstract class MapAIReference implements AIReference {
    private Map<String, Object> values;

    public MapAIReference(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public <T> T getValue(String taskId, String name, Class<T> clazz) {
        return (T) values.get(getKey(taskId, name));
    }

    @Override
    public void removeValue(String taskId, String name) {
        values.remove(getKey(taskId, name));
    }

    @Override
    public void setValue(String taskId, String name, Object value) {
        values.put(getKey(taskId, name), value);
    }

    private String getKey(String taskId, String name) {
        return taskId + "." + name;
    }
}
