package com.gempukku.secsy.context.system;

import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.util.PriorityCollection;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SimpleContext implements SystemContext {
    private static final Logger logger = Logger.getLogger(SimpleContext.class.getName());
    private SystemProducer systemProducer;
    private SystemExtractor systemExtractor;
    private ObjectInitializer objectInitializer;

    private PriorityCollection<LifeCycleSystem> lifeCycleSystems = new PriorityCollection<>();

    private Iterable<Object> ownSystems;
    private Iterable<Object> availableSystems;
    private Map<Class<?>, Object> systemMap;

    private SystemContext parentContext;

    public SimpleContext() {
        this(null);
    }

    public SimpleContext(SystemContext parentContext) {
        this.parentContext = parentContext;
    }

    public void setSystemProducer(SystemProducer systemProducer) {
        this.systemProducer = systemProducer;
    }

    public void setSystemExtractor(SystemExtractor systemExtractor) {
        this.systemExtractor = systemExtractor;
    }

    public void setObjectInitializer(ObjectInitializer objectInitializer) {
        this.objectInitializer = objectInitializer;
    }

    public void startup() {
        ownSystems = systemProducer.createSystems();
        for (Object system : ownSystems) {
            if (system instanceof LifeCycleSystem) {
                lifeCycleSystems.add((LifeCycleSystem) system);
            }
        }

        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.preInitialize();
        }
        availableSystems = ownSystems;

        if (parentContext != null)
            availableSystems = Iterables.concat(ownSystems, parentContext.getSystems());

        systemMap = new HashMap<>(systemExtractor.extractSystems(availableSystems));
        systemMap.put(SystemContext.class, this);
        objectInitializer.initializeObjects(ownSystems, systemMap);

        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            long start = System.currentTimeMillis();
            lifeCycleSystem.initialize();
            long time = System.currentTimeMillis() - start;
            String message = time + "ms - Initialization of " + lifeCycleSystem.getClass().getSimpleName();
            if (time > 100) {
                logger.severe(message);
            } else if (time > 50) {
                logger.warning(message);
            } else {
                logger.fine(message);
            }
        }
        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.postInitialize();
        }
    }

    @Override
    public <T> T getSystem(Class<T> clazz) {
        if (systemMap == null)
            return null;

        return (T) systemMap.get(clazz);
    }

    @Override
    public void initializeObject(Object object) {
        objectInitializer.initializeObjects(Collections.singleton(object), systemMap);
    }

    @Override
    public Iterable<Object> getSystems() {
        return availableSystems;
    }

    public void shutdown() {
        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.preDestroy();
        }
        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.destroy();
        }

        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.postDestroy();
        }

        ownSystems = null;
        systemMap = null;
    }
}
