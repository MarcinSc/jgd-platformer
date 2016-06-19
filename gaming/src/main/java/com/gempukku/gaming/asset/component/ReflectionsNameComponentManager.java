package com.gempukku.gaming.asset.component;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.Component;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        shared = NameComponentManager.class)
public class ReflectionsNameComponentManager implements NameComponentManager, LifeCycleSystem {
    private Map<String, Class<? extends Component>> componentsByName = new HashMap<>();
    private Map<Class<? extends Component>, String> namesByComponent = new HashMap<>();

    @Override
    public void preInitialize() {
        Configuration scanComponents = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(true))
                .setUrls(ClasspathHelper.forJavaClassPath());

        Reflections reflections = new Reflections(scanComponents);
        for (Class<? extends Component> component : reflections.getSubTypesOf(Component.class)) {
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
