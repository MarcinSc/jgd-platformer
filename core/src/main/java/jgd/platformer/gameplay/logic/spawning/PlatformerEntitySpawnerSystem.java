package jgd.platformer.gameplay.logic.spawning;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.spawn.EntitySpawner;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import jgd.platformer.gameplay.component.BaseLocation3DComponent;
import jgd.platformer.gameplay.component.Location3DComponent;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameScreen",
        shared = PlatformerEntitySpawner.class
)
public class PlatformerEntitySpawnerSystem implements PlatformerEntitySpawner {
    @Inject
    private EntitySpawner entitySpawner;

    @Override
    public EntityRef createEntityFromRecipe(String recipe) {
        return entitySpawner.spawnEntityFromRecipe(recipe);
    }

    @Override
    public EntityRef createEntity(String prefabName, Map<String, Object> changes) {
        return entitySpawner.spawnEntity(prefabName, changes);
    }

    @Override
    public EntityRef createEntityFromRecipeAt(float x, float y, float z, String recipe) {
        EntityRef result = entitySpawner.spawnEntityFromRecipe(recipe);

        Location3DComponent location = result.createComponent(Location3DComponent.class);
        location.setLocation(new Vector3(x, y, z));

        if (result.hasComponent(BaseLocation3DComponent.class)) {
            BaseLocation3DComponent baseLocation = result.getComponent(BaseLocation3DComponent.class);
            baseLocation.setLocation(new Vector3(x, y, z));
        }

        result.saveChanges();

        return result;
    }

    @Override
    public EntityRef createEntityAt(float x, float y, float z, String prefabName, Map<String, Object> changes) {
        Map<String, Object> entityDef = new HashMap<>(changes);

        Map<String, Object> locationParams = new HashMap<>();
        locationParams.put("location", x + "," + y + "," + z);

        entityDef.put("+Location3DComponent", locationParams);
        entityDef.put("?BaseLocation3DComponent", locationParams);

        return entitySpawner.spawnEntity(prefabName, entityDef);
    }
}
