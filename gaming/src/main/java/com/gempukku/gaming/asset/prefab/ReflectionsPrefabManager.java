package com.gempukku.gaming.asset.prefab;

import com.gempukku.gaming.asset.component.NameComponentManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.InternalComponentManager;
import com.gempukku.secsy.entity.io.EntityData;
import com.gempukku.secsy.serialization.ComponentInformation;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.Vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "prefabManager",
        shared = PrefabManager.class)
public class ReflectionsPrefabManager implements LifeCycleSystem, PrefabManager {
    @Inject
    private NameComponentManager nameComponentManager;
    @Inject
    private InternalComponentManager internalComponentManager;

    private Map<String, NamedEntityData> prefabsByName;

    @Override
    public float getPriority() {
        return 100;
    }

    @Override
    public void initialize() {
        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(new PrefabsScanner())
                .setUrls(ClasspathHelper.forPackage("prefabs"));

        initializePrefabs(new Reflections(scanConfiguration));
    }

    private void initializePrefabs(Reflections reflections) {
        prefabsByName = new HashMap<>();

        Multimap<String, String> resources = reflections.getStore().get(PrefabsScanner.class);

        for (String prefabName : resources.keySet()) {
            Collection<String> paths = resources.get(prefabName);
            if (paths.size() > 1)
                throw new IllegalStateException("More than one prefab with the same name found: " + prefabName);

            try {
                InputStream prefabInputStream = ReflectionsPrefabManager.class.getResourceAsStream("/" + paths.iterator().next());
                try {
                    NamedEntityData prefabData = readPrefabData(prefabName, prefabInputStream);
                    prefabsByName.put(prefabName, prefabData);
                } finally {
                    prefabInputStream.close();
                }
            } catch (IOException | ParseException exp) {
                throw new RuntimeException("Unable to read prefab data", exp);
            }
        }
    }

    @Override
    public Iterable<? extends EntityData> findPrefabsWithComponents(Class<? extends Component>... components) {
        return Iterables.filter(
                prefabsByName.values(),
                prefabData -> {
                    for (Class<? extends Component> component : components) {
                        if (prefabData.getComponent(component) == null)
                            return false;
                    }
                    return true;
                });
    }

    @Override
    public Iterable<? extends NamedEntityData> findNamedPrefabsWithComponents(Class<? extends Component>... components) {
        return Iterables.filter(
                prefabsByName.values(),
                prefabData -> {
                    for (Class<? extends Component> component : components) {
                        if (prefabData.getComponent(component) == null)
                            return false;
                    }
                    return true;
                });
    }

    @Override
    public EntityData getPrefabByName(String name) {
        return prefabsByName.get(name);
    }

    private NamedEntityData readPrefabData(String prefabName, InputStream prefabInputStream) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject entity = (JSONObject) parser.parse(new InputStreamReader(prefabInputStream, Charset.forName("UTF-8")));

        NamedEntityInformation entityInformation = new NamedEntityInformation(prefabName);
        for (String componentName : (Iterable<String>) entity.keySet()) {
            Class<? extends Component> componentByName = nameComponentManager.getComponentByName(componentName);
            if (componentByName == null)
                throw new IllegalStateException("Unable to find component with name (found in prefab): " + componentName);
            ComponentInformation componentInformation = new ComponentInformation(componentByName);
            Map<String, Class<?>> componentFieldTypes = internalComponentManager.getComponentFieldTypes(componentByName);
            JSONObject componentObject = (JSONObject) entity.get(componentName);
            for (String fieldName : (Iterable<String>) componentObject.keySet()) {
                Object fieldValue = componentObject.get(fieldName);
                if (!componentFieldTypes.containsKey(fieldName))
                    throw new IllegalStateException("Component " + componentName + " does not contain field " + fieldName + " found in prefab " + prefabName);
                componentInformation.addField(fieldName, fieldValue);
            }
            entityInformation.addComponent(componentInformation);
        }
        return entityInformation;
    }

    private static class PrefabsScanner extends ResourcesScanner {
        private String extension = ".prefab";
        private int extensionLength = extension.length();

        public boolean acceptsInput(String file) {
            return file.endsWith(extension);
        }

        public Object scan(Vfs.File file, Object classObject) {
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.length() - extensionLength);
            this.getStore().put(fileName, file.getRelativePath());
            return classObject;
        }
    }
}
