package com.gempukku.gaming.asset.prefab;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.io.EntityData;

public interface PrefabManager {
    Iterable<EntityData> findPrefabsWithComponents(Class<? extends Component>... components);

    EntityData getPrefabByName(String name);
}
