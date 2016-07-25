package com.gempukku.secsy.context.system;

import java.util.Map;

public interface ObjectInitializer {
    void initializeObjects(Iterable<Object> objects, Map<Class<?>, Object> systems);
}
