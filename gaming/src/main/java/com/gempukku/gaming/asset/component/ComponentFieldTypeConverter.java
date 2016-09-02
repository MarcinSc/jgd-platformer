package com.gempukku.gaming.asset.component;

public interface ComponentFieldTypeConverter<T> {
    T convertTo(String value);

    String convertFrom(T value);
}
