package com.gempukku.secsy.context.system;

import com.gempukku.secsy.context.util.Prioritable;

/**
 * Interface for @Systems that want to be notified of context life-cycle.
 */
public interface LifeCycleSystem extends Prioritable {
    /**
     * Called just after the class is instantiated. In this method this system should initialize itself.
     * At this point none of the other systems have been injected yet.
     */
    default void preInitialize() {
    }

    /**
     * Called just after all the other systems have been injected into system.
     * In this method, this system may register itself with any other system.
     */
    default void initialize() {
    }

    /**
     * Called after all systems have been initialized. In this method, this system may act upon all other
     * systems that might have registered itself with this system.
     */
    default void postInitialize() {
    }

    /**
     * Called before any of the systems have been destroyed yet. In this method, this system may act upon all
     * other systems that might have registered itself with this system to revert any actions taken in "postInitialize".
     */
    default void preDestroy() {
    }

    /**
     * Called before this system is destroyed. In this method this system should unregister itself from any
     * other systems it might have registered itself with.
     */
    default void destroy() {
    }

    /**
     * Called after all systems have been destroyed. This is a place where system should clean up after itself.
     */
    default void postDestroy() {
    }

    default float getPriority() {
        return 0;
    }
}
