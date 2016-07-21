package com.gempukku.secsy.serialization;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.io.ComponentData;
import com.gempukku.secsy.entity.io.EntityData;
import com.gempukku.secsy.entity.io.StoredEntityData;

import java.util.LinkedList;
import java.util.List;

public class EntityInformation implements StoredEntityData {
    private int entityId = 0;
    private List<ComponentInformation> components = new LinkedList<>();

    public EntityInformation() {
    }

    public EntityInformation(EntityData toCopy) {
        for (ComponentData componentData : toCopy.getComponents()) {
            addComponent(new ComponentInformation(componentData));
        }
    }


    public Iterable<ComponentInformation> getComponents() {
        return components;
    }

    @Override
    public ComponentData getComponent(Class<? extends Component> componentClass) {
        for (ComponentInformation component : components) {
            if (component.getComponentClass() == componentClass)
                return component;
        }
        return null;
    }

    public void addComponent(ComponentInformation componentInformation) {
        components.add(componentInformation);
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
