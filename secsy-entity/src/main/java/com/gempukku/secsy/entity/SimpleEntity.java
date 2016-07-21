package com.gempukku.secsy.entity;

import com.gempukku.secsy.entity.component.InternalComponentManager;
import com.gempukku.secsy.entity.io.ComponentData;
import com.gempukku.secsy.entity.io.StoredEntityData;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleEntity implements StoredEntityData {
    private InternalComponentManager internalComponentManager;
    public int id;
    public Map<Class<? extends Component>, Component> entityValues = new HashMap<>();
    public boolean exists = true;

    public SimpleEntity(InternalComponentManager internalComponentManager, int id) {
        this.internalComponentManager = internalComponentManager;
        this.id = id;
    }

    @Override
    public int getEntityId() {
        return id;
    }

    @Override
    public Iterable<ComponentData> getComponents() {
        return entityValues.entrySet().stream().map(
                componentEntry -> convertToComponentData(componentEntry.getKey(), componentEntry.getValue())).collect(Collectors.toList());
    }

    @Override
    public ComponentData getComponent(Class<? extends Component> componentClass) {
        Component component = entityValues.get(componentClass);
        if (component == null)
            return null;
        return convertToComponentData(componentClass, component);
    }

    private ComponentData convertToComponentData(Class<? extends Component> componentClass, Component component) {
        return new ComponentData() {
            @Override
            public Class<? extends Component> getComponentClass() {
                return componentClass;
            }

            @Override
            public Map<String, Object> getFields() {
                Map<String, Object> result = new HashMap<String, Object>();
                internalComponentManager.getComponentFieldTypes(component).entrySet().stream().forEach(
                        fieldDef -> {
                            String fieldName = fieldDef.getKey();
                            Object fieldValue = internalComponentManager.getComponentFieldValue(component, fieldName, componentClass);
                            result.put(fieldName, fieldValue);
                        });
                return result;
            }
        };
    }
}
