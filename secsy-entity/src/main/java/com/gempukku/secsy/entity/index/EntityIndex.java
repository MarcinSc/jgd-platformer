package com.gempukku.secsy.entity.index;

import com.gempukku.secsy.entity.EntityRef;

public interface EntityIndex {
    Iterable<EntityRef> getEntities();
}
