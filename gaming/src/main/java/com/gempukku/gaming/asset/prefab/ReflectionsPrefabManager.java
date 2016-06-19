package com.gempukku.gaming.asset.prefab;

import com.gempukku.gaming.asset.component.NameComponentManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.io.EntityData;
import com.gempukku.secsy.serialization.ComponentInformation;
import com.gempukku.secsy.serialization.EntityInformation;
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
public class ReflectionsPrefabManager implements PrefabManager {
    @Inject
    private NameComponentManager nameComponentManager;

    private Map<String, EntityData> prefabsByName;

    private synchronized void init() {
        if (prefabsByName == null) {
            prefabsByName = new HashMap<>();
            Configuration scanPrefabs = new ConfigurationBuilder()
                    .setScanners(new PrefabsScanner())
                    .setUrls(ClasspathHelper.forJavaClassPath());

            Reflections reflections = new Reflections(scanPrefabs);
            Multimap<String, String> resources = reflections.getStore().get(PrefabsScanner.class);

            for (String prefabName : resources.keySet()) {
                Collection<String> paths = resources.get(prefabName);
                if (paths.size() > 1)
                    throw new IllegalStateException("More than one prefab with the same name found: " + prefabName);

                try {
                    InputStream prefabInputStream = ReflectionsPrefabManager.class.getResourceAsStream("/" + paths.iterator().next());
                    try {
                        EntityData prefabData = readPrefabData(prefabInputStream);
                        prefabsByName.put(prefabName, prefabData);
                    } finally {
                        prefabInputStream.close();
                    }
                } catch (IOException | ParseException exp) {
                    throw new RuntimeException("Unable to read prefab data", exp);
                }
            }
        }
    }

    @Override
    public Iterable<EntityData> findPrefabsWithComponents(Class<? extends Component>... components) {
        init();
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
        init();
        return prefabsByName.get(name);
    }

    private EntityData readPrefabData(InputStream prefabInputStream) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject entity = (JSONObject) parser.parse(new InputStreamReader(prefabInputStream, Charset.forName("UTF-8")));

        EntityInformation entityInformation = new EntityInformation();
        for (String componentName : (Iterable<String>) entity.keySet()) {
            Class<? extends Component> componentByName = nameComponentManager.getComponentByName(componentName);
            if (componentByName == null)
                throw new IllegalStateException("Unable to find component with name (found in prefab): " + componentName);
            ComponentInformation componentInformation = new ComponentInformation(componentByName);
            JSONObject componentObject = (JSONObject) entity.get(componentName);
            for (String fieldName : (Iterable<String>) componentObject.keySet()) {
                Object fieldValue = componentObject.get(fieldName);
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
