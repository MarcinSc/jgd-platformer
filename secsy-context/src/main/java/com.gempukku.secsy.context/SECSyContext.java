package com.gempukku.secsy.context;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.ReflectionsAnnotatedTypesSystemProducer;
import com.gempukku.secsy.context.system.ShareSystemInitializer;
import com.gempukku.secsy.context.system.SimpleContext;

import java.net.URL;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public class SECSyContext extends SimpleContext {
    public SECSyContext(SystemContext parentContext, Set<String> profilesActive, Collection<URL> urlsToScan) {
        super(parentContext);
        ReflectionsAnnotatedTypesSystemProducer producer = new ReflectionsAnnotatedTypesSystemProducer(RegisterSystem.class,
                new ProfilesActivePredicate(profilesActive));

        producer.scanReflections(urlsToScan);

        setSystemProducer(producer);
        ShareSystemInitializer initializer = new ShareSystemInitializer();
        setObjectInitializer(initializer);
        setSystemExtractor(initializer);
    }

    public SECSyContext(Set<String> profilesActive, Collection<URL> urlsToScan) {
        this(null, profilesActive, urlsToScan);
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
