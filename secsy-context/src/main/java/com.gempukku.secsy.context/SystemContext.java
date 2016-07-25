package com.gempukku.secsy.context;

/**
 * Context allowing access to @RegisterSystem objects via their shared interfaces.
 */
public interface SystemContext {
    /**
     * Returns a @RegisterSystem object from this context that is registered to share its interface.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getSystem(Class<T> clazz);

    void initializeObject(Object object);

    Iterable<Object> getSystems();
}
