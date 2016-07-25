package com.gempukku.gaming.asset.reflections;

import com.gempukku.secsy.entity.event.Event;
import org.reflections.Reflections;

public class ReflectionsScanned extends Event {
    private Reflections reflections;

    public ReflectionsScanned(Reflections reflections) {
        this.reflections = reflections;
    }

    public Reflections getReflections() {
        return reflections;
    }
}
