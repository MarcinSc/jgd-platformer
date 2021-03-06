package com.gempukku.secsy.entity.component;

public interface ComponentFieldConverter {
    <T> T convertTo(String value, Class<T> clazz);

    <T> String convertFrom(T value, Class<T> clazz);
}
