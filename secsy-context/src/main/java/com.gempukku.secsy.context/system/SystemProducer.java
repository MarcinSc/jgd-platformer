package com.gempukku.secsy.context.system;

/**
 * Interface that produces systems that should participate in a context.
 */
public interface SystemProducer {
    Iterable<Object> createSystems();
}
