package com.gempukku.secsy.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for classes that want to participate in a context.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RegisterSystem {
    /**
     * List of profiles this system should be included in when gathering data for context.
     * This system will be included only if all its profiles are activated for the context.
     * If this returns an empty array (default) it will be included in all contexts generated.
     *
     * @return
     */
    String[] profiles() default {};

    /**
     * List of interfaces this system exposes to other systems participating in a context.
     * The class annotated should implement all the interfaces that this annotation contains.
     *
     * @return
     */
    Class<?>[] shared() default {};
}
