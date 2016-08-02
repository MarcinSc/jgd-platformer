package com.gempukku.gaming.interpolation;

import com.badlogic.gdx.math.Interpolation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class InterpolationUtil {
    private static Map<String, Interpolation> interpolationMap = new HashMap<>();

    static {
        for (Field field : Interpolation.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                try {
                    interpolationMap.put(field.getName().toLowerCase(), (Interpolation) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to get Interpolation", e);
                }
            }
        }
    }

    private InterpolationUtil() {
    }

    public static Interpolation getInterpolation(String name) {
        if (name == null)
            return Interpolation.linear;
        return interpolationMap.get(name.toLowerCase());
    }
}
