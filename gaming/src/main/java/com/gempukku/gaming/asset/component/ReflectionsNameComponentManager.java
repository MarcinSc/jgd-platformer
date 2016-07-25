package com.gempukku.gaming.asset.component;

import com.gempukku.gaming.asset.reflections.GatherReflectionScanners;
import com.gempukku.gaming.asset.reflections.ReflectionsScanned;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import org.reflections.ReflectionUtils;
import org.reflections.scanners.AbstractScanner;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RegisterSystem(
        shared = NameComponentManager.class)
public class ReflectionsNameComponentManager implements NameComponentManager {
    private Map<String, Class<? extends Component>> componentsByName = new HashMap<>();
    private Map<Class<? extends Component>, String> namesByComponent = new HashMap<>();

    @ReceiveEvent
    public void createScanner(GatherReflectionScanners event, EntityRef entityRef) {
        event.addScanner(new ComponentScanner());
    }

    @ReceiveEvent(priority = 100)
    public void getComponents(ReflectionsScanned event, EntityRef entityRef) {
        Collection<String> componentClassNames = event.getReflections().getStore().get(ComponentScanner.class).get(Component.class.getName());
        List<Class<? extends Component>> components = ReflectionUtils.forNames(componentClassNames, event.getReflections().getConfiguration().getClassLoaders());
        for (Class<? extends Component> component : components) {
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

    private static class ComponentScanner extends AbstractScanner {
        private String componentClassName = Component.class.getName();

        @Override
        public void scan(Object o) {
            List<String> interfacesNames = getMetadataAdapter().getInterfacesNames(o);
            if (interfacesNames.contains(componentClassName))
                getStore().put(componentClassName, getMetadataAdapter().getClassName(o));
        }
    }
}
