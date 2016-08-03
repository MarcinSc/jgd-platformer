package com.gempukku.gaming.spawn;

import com.gempukku.secsy.entity.EntityRef;

import java.util.Map;

public interface EntitySpawner {
    EntityRef spawnEntityFromRecipe(String recipe);

    EntityRef spawnEntity(String prefabName, Map<String, Object> changes);
}
