package com.gempukku.secsy.context.system;

import java.util.Map;

public interface SystemExtractor<S> {
    Map<Class<?>, S> extractSystems(Iterable<S> systems);
}
