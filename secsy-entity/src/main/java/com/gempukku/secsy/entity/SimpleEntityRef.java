package com.gempukku.secsy.entity;

import com.gempukku.secsy.entity.component.InternalComponentManager;
import com.gempukku.secsy.entity.event.*;

import java.util.*;

public class SimpleEntityRef implements EntityRef {
    private InternalComponentManager internalComponentManager;
    private EntityListener entityListener;
    private EntityEventListener entityEventListener;
    private SimpleEntity entity;

    private Set<Class<? extends Component>> newComponents = new HashSet<>();
    private Map<Class<? extends Component>, Component> usedComponents = new HashMap<>();
    private Set<Class<? extends Component>> removedComponents = new HashSet<>();
    private boolean readOnly;

    public SimpleEntityRef(InternalComponentManager internalComponentManager,
                           EntityListener entityListener,
                           EntityEventListener entityEventListener,
                           SimpleEntity entity, boolean readOnly) {
        this.internalComponentManager = internalComponentManager;
        this.entityListener = entityListener;
        this.entityEventListener = entityEventListener;
        this.entity = entity;
        this.readOnly = readOnly;
    }

    public SimpleEntity getEntity() {
        return entity;
    }

    @Override
    public <T extends Component> T createComponent(Class<T> clazz) {
        validateWritable();
        if (usedComponents.containsKey(clazz) || entity.entityValues.containsKey(clazz))
            throw new IllegalStateException("This entity ref already has this component defined");

        T component = internalComponentManager.createComponent(this, clazz);
        newComponents.add(clazz);
        usedComponents.put(clazz, component);

        return component;
    }

    @Override
    public <T extends Component> T getComponent(Class<T> clazz) {
        // First check if this EntityRef already has a component of that class to work with
        Component component = usedComponents.get(clazz);
        if (component != null)
            return (T) component;

        T originalComponent = (T) entity.entityValues.get(clazz);
        if (originalComponent == null)
            return null;

        T localComponent = internalComponentManager.copyComponent(this, originalComponent);
        if (readOnly)
            localComponent = internalComponentManager.copyComponentUnmodifiable(localComponent, true);
        usedComponents.put(clazz, localComponent);
        return localComponent;
    }

    @Override
    public void saveChanges() {
        // Validation
        validateWritable();

        for (Class<? extends Component> componentClass : removedComponents) {
            Component originalComponent = entity.entityValues.get(componentClass);
            if (originalComponent == null)
                throw new IllegalStateException("This entity does not contain a component of that class");
        }

        for (Map.Entry<Class<? extends Component>, Component> componentEntry : usedComponents.entrySet()) {
            Class<? extends Component> clazz = componentEntry.getKey();
            if (newComponents.contains(clazz)) {
                if (entity.entityValues.containsKey(clazz))
                    throw new IllegalStateException("This entity already contains a component of that class");
            } else {
                if (!entity.entityValues.containsKey(clazz))
                    throw new IllegalStateException("This entity does not contain a component of that class");
            }
        }

        if (!removedComponents.isEmpty()) {
            Map<Class<? extends Component>, Component> removedComponentsMap = new HashMap<>();
            for (Class<? extends Component> removedComponent : removedComponents) {
                Component componentValue = entity.entityValues.get(removedComponent);
                removedComponentsMap.put(removedComponent, internalComponentManager.copyComponentUnmodifiable(componentValue, false));
            }

            BeforeComponentRemoved beforeRemovedEvent = new BeforeComponentRemoved(removedComponentsMap);
            entityEventListener.eventSent(this, beforeRemovedEvent);
        }

        // Actual data changing
        Map<Class<? extends Component>, Component> removedComponentsMap = new HashMap<>();
        for (Class<? extends Component> componentClass : removedComponents) {
            Component componentValue = entity.entityValues.remove(componentClass);
            usedComponents.remove(componentClass);
            removedComponentsMap.put(componentClass, internalComponentManager.copyComponentUnmodifiable(componentValue, false));
        }

        Map<Class<? extends Component>, Component> addedComponents = new HashMap<>();

        for (Component component : usedComponents.values()) {
            final Class<Component> clazz = internalComponentManager.getComponentClass(component);
            if (newComponents.contains(clazz)) {
                Component storedComponent = internalComponentManager.copyComponent(null, component);
                entity.entityValues.put(clazz, storedComponent);

                internalComponentManager.saveComponent(storedComponent, component);

                addedComponents.put(clazz, internalComponentManager.copyComponentUnmodifiable(component, false));
            }
        }

        Map<Class<? extends Component>, Component> updatedComponentsOld = new HashMap<>();
        Map<Class<? extends Component>, Component> updatedComponentsNew = new HashMap<>();

        for (Component component : usedComponents.values()) {
            final Class<Component> clazz = internalComponentManager.getComponentClass(component);
            if (!newComponents.contains(clazz)) {
                Component originalComponent = entity.entityValues.get(clazz);

                updatedComponentsOld.put(clazz, internalComponentManager.copyComponentUnmodifiable(originalComponent, false));

                internalComponentManager.saveComponent(originalComponent, component);

                updatedComponentsNew.put(clazz, internalComponentManager.copyComponentUnmodifiable(originalComponent, false));
            }
        }

        removedComponents.clear();
        newComponents.clear();

        entityListener.entitiesModified(Collections.singleton(entity));

        if (!removedComponentsMap.isEmpty()) {
            AfterComponentRemoved afterRemovedEvent = new AfterComponentRemoved(removedComponentsMap);
            entityEventListener.eventSent(this, afterRemovedEvent);
        }

        if (!addedComponents.isEmpty()) {
            AfterComponentAdded event = new AfterComponentAdded(addedComponents);
            entityEventListener.eventSent(this, event);
        }

        if (!updatedComponentsOld.isEmpty()) {
            AfterComponentUpdated event = new AfterComponentUpdated(updatedComponentsOld, updatedComponentsNew);
            entityEventListener.eventSent(this, event);
        }
    }

    @Override
    public void removeComponents(Class<? extends Component>... clazz) {
        validateWritable();
        for (Class<? extends Component> tClass : clazz) {
            removedComponents.add(tClass);
        }
    }

    @Override
    public Iterable<Class<? extends Component>> listComponents() {
        return Collections.unmodifiableCollection(entity.entityValues.keySet());
    }

    @Override
    public boolean hasComponent(Class<? extends Component> component) {
        return entity.entityValues.containsKey(component);
    }

    @Override
    public boolean exists() {
        return entity.exists;
    }

    @Override
    public void send(Event event) {
        validateWritable();
        entityEventListener.eventSent(this, event);
    }

    private void validateWritable() {
        if (readOnly)
            throw new IllegalStateException("This entity is in read only mode");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleEntityRef that = (SimpleEntityRef) o;

        return entity.equals(that.entity);

    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }
}