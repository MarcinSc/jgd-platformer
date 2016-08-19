package com.gempukku.gaming.ai;

import com.gempukku.gaming.ai.builder.JsonTaskBuilder;
import com.gempukku.gaming.asset.JavaPackageProvider;
import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.google.common.collect.Multimap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.Vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(
        profiles = "ai",
        shared = AIEngine.class
)
public class AISystem implements LifeCycleSystem, AIEngine {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private SystemContext context;
    @Inject
    private JavaPackageProvider javaPackageProvider;

    private EntityIndex aiEntities;

    private Map<String, JSONObject> behaviorJsons = new HashMap<>();
    private Map<String, RootTask<EntityRefReference>> compiledAIs = new HashMap<>();
    private Map<String, Class<? extends AITask<EntityRefReference>>> taskTypes = new HashMap<>();

    @Override
    public float getPriority() {
        return 100;
    }

    @Override
    public void initialize() {
        Configuration behaviorsConfiguration = new ConfigurationBuilder()
                .setScanners(new BehaviorScanner())
                .setUrls(ClasspathHelper.forPackage("behaviors", ClasspathHelper.contextClassLoader()));

        loadAllBehaviorJsons(new Reflections(behaviorsConfiguration));

        Set<URL> contextLocations = new HashSet<>();
        for (String javaPackage : javaPackageProvider.getJavaPackages()) {
            contextLocations.addAll(ClasspathHelper.forPackage(javaPackage, ClasspathHelper.contextClassLoader()));
        }

        Configuration taskConfiguration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(contextLocations);

        findAllAITasks(new Reflections(taskConfiguration));

        aiEntities = entityIndexManager.addIndexOnComponents(AIComponent.class);
    }

    @Override
    public Iterable<AITask<EntityRefReference>> getRunningTasks(EntityRef entityRef) {
        AIComponent ai = entityRef.getComponent(AIComponent.class);
        String aiName = ai.getAiName();
        RootTask<EntityRefReference> aiRootTask = compiledAIs.get(aiName);
        return aiRootTask.getRunningTasks(new EntityRefReference(entityRef));
    }

    @Override
    public <T extends AITask<EntityRefReference>> Iterable<T> getRunningTasksOfType(EntityRef entityRef, Class<T> clazz) {
        Set<T> result = new HashSet<>();
        for (AITask<EntityRefReference> entityRefReferenceAITask : getRunningTasks(entityRef)) {
            if (entityRefReferenceAITask.getClass() == clazz)
                result.add((T) entityRefReferenceAITask);
        }
        return result;
    }

    @Override
    public EntityRefReference getReference(EntityRef entityRef) {
        return new EntityRefReference(entityRef);
    }

    private void loadAllBehaviorJsons(Reflections reflections) {
        Multimap<String, String> resources = reflections.getStore().get(BehaviorScanner.class);

        for (String behaviorName : resources.keySet()) {
            Collection<String> paths = resources.get(behaviorName);
            if (paths.size() > 1)
                throw new IllegalStateException("More than one behavior with the same name found: " + behaviorName);

            try {
                InputStream behaviorInputStream = AISystem.class.getResourceAsStream("/" + paths.iterator().next());
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject entity = (JSONObject) parser.parse(new InputStreamReader(behaviorInputStream, Charset.forName("UTF-8")));
                    behaviorJsons.put(behaviorName, entity);
                } finally {
                    behaviorInputStream.close();
                }
            } catch (IOException | ParseException exp) {
                throw new RuntimeException("Unable to read behavior data", exp);
            }
        }
    }

    private void findAllAITasks(Reflections reflections) {
        Set<Class<? extends AITask>> aiTasks = reflections.getSubTypesOf(AITask.class);

        for (Class<? extends AITask> aiTask : aiTasks) {
            String simpleName = aiTask.getSimpleName();
            Class<? extends AITask<EntityRefReference>> aiTaskClass = (Class<? extends AITask<EntityRefReference>>) aiTask;
            taskTypes.put(simpleName, aiTaskClass);
            if (simpleName.endsWith("Task"))
                taskTypes.put(simpleName.substring(0, simpleName.length() - 4), aiTaskClass);
        }
    }

    @ReceiveEvent
    public void processAI(GameLoopUpdate event, EntityRef entityRef) {
        for (EntityRef aiEntity : aiEntities) {
            AIComponent ai = aiEntity.getComponent(AIComponent.class);
            String aiName = ai.getAiName();
            RootTask<EntityRefReference> aiRootTask = compiledAIs.get(aiName);
            if (aiRootTask == null) {
                aiRootTask = createAi(aiName);
                compiledAIs.put(aiName, aiRootTask);
            }
            EntityRefReference reference = new EntityRefReference(aiEntity);
            aiRootTask.processAI(reference);
            reference.storeValues();
        }
    }

    private RootTask<EntityRefReference> createAi(String aiName) {
        JSONObject behaviorData = behaviorJsons.get(aiName);

        JsonTaskBuilder<EntityRefReference> builder = new JsonTaskBuilder<EntityRefReference>(context, behaviorJsons, taskTypes);
        String rootTaskId = builder.getNextId();
        AITask<EntityRefReference> aiTask = builder.buildTask(null, behaviorData);

        return new RootTask<EntityRefReference>(rootTaskId, aiTask);
    }

    private static class BehaviorScanner extends ResourcesScanner {
        private String extension = ".behavior";
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
