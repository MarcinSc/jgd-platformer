package com.gempukku.secsy.context;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.ReflectionsAnnotatedTypesSystemProducer;
import com.gempukku.secsy.context.system.ShareSystemInitializer;
import com.gempukku.secsy.context.system.SimpleContext;
import org.reflections.Reflections;

import java.util.Set;
import java.util.function.Predicate;

public class SECSyContext extends SimpleContext<Object> {
    public SECSyContext(Set<String> profilesActive, Reflections... reflectionsToScan) {
        ReflectionsAnnotatedTypesSystemProducer producer = new ReflectionsAnnotatedTypesSystemProducer(RegisterSystem.class,
                new ProfilesActivePredicate(profilesActive));

        for (Reflections reflections : reflectionsToScan) {
            producer.scanReflections(reflections);
        }

        setSystemProducer(producer);
        ShareSystemInitializer<Object> initializer = new ShareSystemInitializer<>();
        setObjectInitializer(initializer);
        setSystemExtractor(initializer);
    }

    private static class ProfilesActivePredicate implements Predicate<Class<?>> {
        private Set<String> profilesActive;

        public ProfilesActivePredicate(Set<String> profilesActive) {
            this.profilesActive = profilesActive;
        }

        @Override
        public boolean test(Class<?> systemClass) {
            final RegisterSystem registerSystemAnnotation = systemClass.getAnnotation(RegisterSystem.class);
            for (String profileRequired : registerSystemAnnotation.profiles()) {
                if (!profilesActive.contains(profileRequired))
                    return false;
            }

            return true;
        }
    }
}
