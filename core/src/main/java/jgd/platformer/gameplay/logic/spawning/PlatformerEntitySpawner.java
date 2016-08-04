package jgd.platformer.gameplay.logic.spawning;

import com.gempukku.secsy.entity.EntityRef;

import java.util.Map;

public interface PlatformerEntitySpawner {
    EntityRef createEntityFromRecipe(String additionalObject);

    EntityRef createEntityFromRecipeAt(float x, float y, float z, String recipe);

    EntityRef createEntityAt(float x, float y, float z, String prefabName, Map<String, Object> changes);
}
