package com.gempukku.secsy.context.system;

import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.util.PriorityCollection;

import java.util.Map;

public class SimpleContext<S> implements SystemContext<S> {
    private SystemProducer<S> systemProducer;
    private SystemInitializer<S> systemInitializer;

    private PriorityCollection<LifeCycleSystem> lifeCycleSystems = new PriorityCollection<>();

    private Iterable<S> systems;
    private Map<Class<?>, S> systemMap;

    public void setSystemProducer(SystemProducer<S> systemProducer) {
        this.systemProducer = systemProducer;
    }

    public void setSystemInitializer(SystemInitializer<S> systemInitializer) {
        this.systemInitializer = systemInitializer;
    }

    public void startup() {
        systems = systemProducer.createSystems();
        for (S system : systems) {
            if (system instanceof LifeCycleSystem) {
                lifeCycleSystems.add((LifeCycleSystem) system);
            }
        }

        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.preInitialize();
        }

        systemMap = systemInitializer.initializeSystems(systems);

        for (S system : systems) {
            if (system instanceof ContextAwareSystem) {
                ((ContextAwareSystem<S>) system).setContext(this);
            }
        }

        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.initialize();
        }
        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.postInitialize();
        }
    }

    @Override
    public <T extends S> T getSystem(Class<T> clazz) {
        if (systemMap == null)
            return null;

        return (T) systemMap.get(clazz);
    }

    @Override
    public Iterable<S> getSystems() {
        return systems;
    }

    public void shutdown() {
        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.preDestroy();
        }
        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.destroy();
        }

        systemInitializer.destroySystems(systems);

        for (LifeCycleSystem lifeCycleSystem : lifeCycleSystems) {
            lifeCycleSystem.postDestroy();
        }

        systems = null;
        systemMap = null;
    }
}
