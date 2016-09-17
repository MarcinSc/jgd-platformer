package com.gempukku.secsy.entity.component;

public interface EntityComponentFieldHandler {
    <T> T copyFromEntity(T value, Class<T> clazz);

    <T> T storeIntoEntity(T oldValue, T newValue, Class<T> clazz);
}
