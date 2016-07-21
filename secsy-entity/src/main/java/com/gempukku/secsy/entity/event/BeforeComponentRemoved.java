package com.gempukku.secsy.entity.event;

import com.gempukku.secsy.entity.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class BeforeComponentRemoved extends Event implements ComponentEvent {
    private Map<Class<? extends Component>, Component> components;

    public BeforeComponentRemoved(Map<Class<? extends Component>, Component> components) {
        this.components = components;
    }

    public Collection<Class<? extends Component>> getComponents() {
        return Collections.unmodifiableCollection(components.keySet());
    }

    public <T> T getComponent(Class<T> clazz) {
        //noinspection unchecked
        return (T) components.get(clazz);
    }
}
