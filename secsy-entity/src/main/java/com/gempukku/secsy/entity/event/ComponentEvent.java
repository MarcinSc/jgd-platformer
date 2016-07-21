package com.gempukku.secsy.entity.event;

import com.gempukku.secsy.entity.Component;

import java.util.Collection;

public interface ComponentEvent {
    Collection<Class<? extends Component>> getComponents();
}
