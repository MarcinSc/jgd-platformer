package com.gempukku.secsy.entity.component;

import com.gempukku.secsy.context.annotation.API;
import com.gempukku.secsy.entity.Component;

/**
 * Interface for a class responsible for creating objects storing data for components in memory.
 */
@API
public interface ComponentManager {
    boolean hasSameValues(Component component1, Component component2);
}
