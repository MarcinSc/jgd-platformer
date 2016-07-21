package com.gempukku.secsy.entity;

import com.gempukku.secsy.entity.event.Event;

/**
 * Interface for a class that wants to listen for events associated with entities.
 */
public interface EntityEventListener {
    /**
     * Called when an event was sent to the entity.
     *
     * @param entity
     * @param event
     */
    void eventSent(EntityRef entity, Event event);
}
