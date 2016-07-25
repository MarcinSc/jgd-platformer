package com.gempukku.gaming.asset.component;

import com.gempukku.gaming.asset.reflections.GatherReflectionScanners;
import com.gempukku.gaming.asset.reflections.ReflectionsScanned;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import org.reflections.scanners.SubTypesScanner;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        shared = NameComponentManager.class)
public class ReflectionsNameComponentManager implements NameComponentManager {
    private Map<String, Class<? extends Component>> componentsByName = new HashMap<>();
    private Map<Class<? extends Component>, String> namesByComponent = new HashMap<>();

    @ReceiveEvent
    public void createScanner(GatherReflectionScanners event, EntityRef entityRef) {
        event.addScanner(new SubTypesScanner(true));
    }

    @ReceiveEvent(priority = 100)
    public void getComponents(ReflectionsScanned event, EntityRef entityRef) {
        for (Class<? extends Component> component : event.getReflections().getSubTypesOf(Component.class)) {
            String simpleName = component.getSimpleName();
            componentsByName.put(simpleName, component);
            namesByComponent.put(component, simpleName);
        }
    }

    @Override
    public Class<? extends Component> getComponentByName(String name) {
        return componentsByName.get(name);
    }

    @Override
    public String getNameByComponent(Class<? extends Component> componentClass) {
        return namesByComponent.get(componentClass);
    }
}
