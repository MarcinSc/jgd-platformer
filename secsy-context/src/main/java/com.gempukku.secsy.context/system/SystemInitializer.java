package com.gempukku.secsy.context.system;

import java.util.Map;

/**
 * Interface that takes care of initializing and destroying classes.
 *
 * @param <S>
 */
public interface SystemInitializer<S> {
    Map<Class<?>, S> initializeSystems(Iterable<S> systems);

    void destroySystems(Iterable<S> systems);
}
