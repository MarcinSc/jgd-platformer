package com.gempukku.secsy.entity.dispatch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReceiveEvent {
    float priority() default 0;

    String priorityName() default "";
}
