package com.gempukku.gaming.asset.component;

import com.gempukku.gaming.asset.JavaPackageProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.Component;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.AbstractScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RegisterSystem(
        shared = NameComponentManager.class)
public class ReflectionsNameComponentManager implements LifeCycleSystem, NameComponentManager {
    @Inject
    private JavaPackageProvider javaPackageProvider;

    private Map<String, Class<? extends Component>> componentsByName = new HashMap<>();
    private Map<Class<? extends Component>, String> namesByComponent = new HashMap<>();

    @Override
    public float getPriority() {
        return 200;
    }

    @Override
    public void initialize() {
        Set<URL> contextLocations = new HashSet<>();
        for (String javaPackage : javaPackageProvider.getJavaPackages()) {
            contextLocations.addAll(ClasspathHelper.forPackage(javaPackage, ClasspathHelper.contextClassLoader()));
        }

        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(contextLocations);

        Reflections reflections = new Reflections(scanConfiguration);
        Set<Class<? extends Component>> components = reflections.getSubTypesOf(Component.class);
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
