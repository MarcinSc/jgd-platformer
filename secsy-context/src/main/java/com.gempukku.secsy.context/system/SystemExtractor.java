package com.gempukku.secsy.context.system;

import java.util.Map;

public interface SystemExtractor {
    Map<Class<?>, Object> extractSystems(Iterable<Object> systems);
}
