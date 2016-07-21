package com.gempukku.secsy.entity.event;

import com.gempukku.secsy.entity.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class AfterComponentUpdated extends Event implements ComponentEvent {
    private Map<Class<? extends Component>, Component> oldComponents;
    private Map<Class<? extends Component>, Component> newComponents;

    public AfterComponentUpdated(Map<Class<? extends Component>, Component> oldComponents,
                                 Map<Class<? extends Component>, Component> newComponents) {
        this.oldComponents = oldComponents;
        this.newComponents = newComponents;
    }

    public Collection<Class<? extends Component>> getComponents() {
        return Collections.unmodifiableCollection(oldComponents.keySet());
    }

    public <T> T getOldComponent(Class<T> clazz) {
        //noinspection unchecked
        return (T) oldComponents.get(clazz);
    }

    public <T> T getNewComponent(Class<T> clazz) {
        //noinspection unchecked
        return (T) newComponents.get(clazz);
    }
}
