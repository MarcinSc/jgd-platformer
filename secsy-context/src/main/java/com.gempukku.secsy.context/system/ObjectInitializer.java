package com.gempukku.secsy.context.system;

import java.util.Map;

public interface ObjectInitializer<S> {
    void initializeObjects(Iterable<?> objects, Map<Class<?>, S> systems);
}
