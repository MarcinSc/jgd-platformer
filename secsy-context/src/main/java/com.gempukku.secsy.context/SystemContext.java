package com.gempukku.secsy.context;

/**
 * Context allowing access to @RegisterSystem objects via their shared interfaces.
 */
public interface SystemContext<S> {
    /**
     * Returns a @RegisterSystem object from this context that is registered to share its interface.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T extends S> T getSystem(Class<T> clazz);

    void initializeObject(Object object);

    Iterable<S> getSystems();
}
