package com.gempukku.secsy.entity;

import com.gempukku.secsy.context.annotation.API;
import com.gempukku.secsy.entity.event.Event;

/**
 * Reference to an entity that allows to query, store, update and remove components from it, as well
 * as send events to it.
 * An entity reference might have some pending changes on it.
 * It is not recommended to pass EntityRefs between systems using events.
 */
@API
public interface EntityRef {
    /**
     * Creates a new component of the specified class if this reference does not have one yet.
     * If it has one already, throws an exception.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T extends Component> T createComponent(Class<T> clazz);

    /**
     * Gets an existing component (or returns null) for this entity.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T extends Component> T getComponent(Class<T> clazz);

    /**
     * Lists components for this entity.
     *
     * @return
     */
    Iterable<Class<? extends Component>> listComponents();

    /**
     * Checks if that entity has the component.
     *
     * @param component
     * @return
     */
    boolean hasComponent(Class<? extends Component> component);

    /**
     * Checks if this entity still exists.
     *
     * @return
     */
    boolean exists();

    /**
     * Saves changes (or adds) done to this reference to its actual entity.
     */
    void saveChanges();

    /**
     * Removes specified components from the underlying entity.
     *
     * @param clazz
     * @param <T>
     */
    void removeComponents(Class<? extends Component>... clazz);

    /**
     * Sends an event to an entity.
     *
     * @param event
     */
    void send(Event event);
}
