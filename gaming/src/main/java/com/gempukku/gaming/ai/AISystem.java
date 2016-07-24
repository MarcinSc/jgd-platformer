package com.gempukku.gaming.ai;

import com.gempukku.gaming.ai.builder.JsonTaskBuilder;
import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.ContextAwareSystem;
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
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "ai"
)
public class AISystem implements LifeCycleSystem, ContextAwareSystem<Object> {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex aiEntities;

    private Map<String, JSONObject> behaviorJsons = new HashMap<>();
    private Map<String, RootTask> compiledAIs = new HashMap<>();
    private Map<String, Class<? extends AITask>> taskTypes = new HashMap<>();
    private SystemContext<Object> context;

    @Override
    public void setContext(SystemContext<Object> context) {
        this.context = context;
    }

    @Override
    public void initialize() {
        aiEntities = entityIndexManager.addIndexOnComponents(AIComponent.class);

        findAllAITasks();

        loadAllBehaviorJsons();
    }

    private void loadAllBehaviorJsons() {
        Configuration scanBehaviors = new ConfigurationBuilder()
                .setScanners(new BehaviorScanner())
                .setUrls(ClasspathHelper.forJavaClassPath());

        Reflections reflections = new Reflections(scanBehaviors);
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

    private void findAllAITasks() {
        Configuration scanComponents = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(true))
                .setUrls(ClasspathHelper.forJavaClassPath());

        Reflections reflections = new Reflections(scanComponents);
        for (Class<? extends AITask> aiTasks : reflections.getSubTypesOf(AITask.class)) {
            String simpleName = aiTasks.getSimpleName();
            taskTypes.put(simpleName, aiTasks);
            if (simpleName.endsWith("Task"))
                taskTypes.put(simpleName.substring(0, simpleName.length() - 4), aiTasks);
        }
    }

    @ReceiveEvent
    public void processAI(GameLoopUpdate event, EntityRef entityRef) {
        for (EntityRef aiEntity : aiEntities) {
            AIComponent ai = aiEntity.getComponent(AIComponent.class);
            String aiName = ai.getAiName();
            RootTask aiRootTask = compiledAIs.get(aiName);
            if (aiRootTask == null) {
                aiRootTask = createAi(aiName);
                compiledAIs.put(aiName, aiRootTask);
            }
            EntityRefReference reference = new EntityRefReference(aiEntity);
            aiRootTask.processAI(reference);
            reference.storeValues();
        }
    }

    private RootTask createAi(String aiName) {
        JSONObject behaviorData = behaviorJsons.get(aiName);

        JsonTaskBuilder builder = new JsonTaskBuilder(context, behaviorJsons, taskTypes);
        String rootTaskId = builder.getNextId();
        AITask aiTask = builder.buildTask(null, behaviorData);

        return new RootTask(rootTaskId, aiTask);
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
