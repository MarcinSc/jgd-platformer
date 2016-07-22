package com.gempukku.secsy.entity.index;

import com.gempukku.secsy.entity.EntityRef;

public interface EntityIndex extends Iterable<EntityRef> {
    Iterable<EntityRef> getEntities();
}
