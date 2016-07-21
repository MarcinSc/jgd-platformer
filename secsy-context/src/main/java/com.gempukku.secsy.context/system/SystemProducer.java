package com.gempukku.secsy.context.system;

/**
 * Interface that produces systems that should participate in a context.
 *
 * @param <S>
 */
public interface SystemProducer<S> {
    Iterable<S> createSystems();
}
