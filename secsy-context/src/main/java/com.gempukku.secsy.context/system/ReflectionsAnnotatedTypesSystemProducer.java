package com.gempukku.secsy.context.system;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ReflectionsAnnotatedTypesSystemProducer implements SystemProducer {
    private Class<? extends Annotation> annotation;
    private Predicate<Class<?>> classPredicate;

    private Set<Class<?>> systemsDetected = new HashSet<>();

    public ReflectionsAnnotatedTypesSystemProducer(Class<? extends Annotation> annotation,
                                                   Predicate<Class<?>> classPredicate) {
        this.annotation = annotation;
        this.classPredicate = classPredicate;
    }

    public void scanReflections(Collection<URL> urlsToScan) {
        Configuration scanBasedOnAnnotations = new ConfigurationBuilder()
                .setScanners(new TypeAnnotationsScanner())
                .setUrls(urlsToScan);

        for (Class<?> type : new Reflections(scanBasedOnAnnotations).getTypesAnnotatedWith(annotation)) {
            if (classPredicate.test(type))
                systemsDetected.add(type);
        }
    }

    @Override
    public Iterable<Object> createSystems() {
        try {
            Set<Object> systems = new HashSet<>();

            for (Class<?> system : systemsDetected) {
                systems.add(system.newInstance());
            }

            return Collections.unmodifiableCollection(systems);
        } catch (IllegalAccessException | InstantiationException exp) {
            throw new RuntimeException("Unable to instantiate systems", exp);
        }
    }
}
