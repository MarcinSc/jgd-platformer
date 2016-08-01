package com.gempukku.gaming.spawn;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.io.ComponentData;
import com.gempukku.secsy.entity.io.EntityData;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModifiedEntityData implements EntityData {
    private Set<ModifiedComponentData> components = new HashSet<>();

    public ModifiedEntityData(EntityData baseEntityData) {
        baseEntityData.getComponents().forEach(component -> components.add(new ModifiedComponentData(component)));
    }

    public boolean removeComponent(Class<? extends Component> component) {
        return components.removeIf(comp -> comp.getComponentClass() == component);
    }

    public boolean containsComponent(Class<? extends Component> component) {
        return getModifiedComponent(component) != null;
    }

    public void addComponent(ComponentData componentData) {
        components.add(new ModifiedComponentData(componentData));
    }

    public ModifiedComponentData modifyComponent(Class<? extends Component> component) {
        return getModifiedComponent(component);
    }

    @Override
    public ComponentData getComponent(Class<? extends Component> componentClass) {
        return getModifiedComponent(componentClass);
    }

    private ModifiedComponentData getModifiedComponent(Class<? extends Component> componentClass) {
        for (ModifiedComponentData component : components) {
            if (component.getComponentClass() == componentClass)
                return component;
        }
        return null;
    }

    @Override
    public Iterable<? extends ComponentData> getComponents() {
        return Collections.unmodifiableSet(components);
    }
}
