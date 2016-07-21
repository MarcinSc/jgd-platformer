package com.gempukku.secsy.entity.index;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.SimpleEntity;

public interface EntityRefCreationCallback {
    EntityRef createEntityRef(SimpleEntity entity);
}
