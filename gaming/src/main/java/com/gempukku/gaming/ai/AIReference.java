package com.gempukku.gaming.ai;

public interface AIReference {
    void setValue(String taskId, String name, Object value);

    void removeValue(String taskId, String name);

    <T> T getValue(String taskId, String name, Class<T> clazz);

    void storeValues();
}
