package com.gempukku.secsy.context.system;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShareSystemInitializer<S> implements ObjectInitializer<S>, SystemExtractor<S> {
    @Override
    public Map<Class<?>, S> extractSystems(Iterable<S> systems) {
        Map<Class<?>, S> context = new HashMap<>();

        // Figure out shared objects
        for (S system : systems) {
            final RegisterSystem registerSystemAnnotation = system.getClass().getAnnotation(RegisterSystem.class);
            if (registerSystemAnnotation != null) {
                for (Class<?> clazz : registerSystemAnnotation.shared()) {
                    if (context.put(clazz, system) != null) {
                        throw new RuntimeException("Context contains multiple systems that expose the same interface: "
                                + clazz.getName());
                    }
                }
            }
        }
        return Collections.unmodifiableMap(context);
    }

    @Override
    public void initializeObjects(Iterable<?> objects, Map<Class<?>, S> systems) {
        // Enrich systems with shared components
        for (Object object : objects) {
            Class<? extends Object> systemClass = object.getClass();
            while (true) {
                initForClass(systems, object, systemClass);
                systemClass = systemClass.getSuperclass();
                if (systemClass == Object.class) {
                    break;
                }
            }
        }
    }

    private void initForClass(Map<Class<?>, S> context, Object system, Class<? extends Object> systemClass) {
        for (Field field : systemClass.getDeclaredFields()) {
            final Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                final Object value = context.get(field.getType());
                if (value != null) {
                    field.setAccessible(true);
                    try {
                        field.set(system, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (!inject.optional())
                        throw new RuntimeException("Unknown objects of type " + field.getType().getName() + " requested in " + systemClass.getName() + ", but none found in context");
                }
            }
        }
    }
}
