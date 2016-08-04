package jgd.platformer.gameplay.logic.spawning;

import com.gempukku.gaming.spawn.EntitySpawner;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import jgd.platformer.gameplay.component.BaseLocationComponent;
import jgd.platformer.gameplay.component.LocationComponent;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameplay",
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
    public EntityRef createEntityFromRecipeAt(float x, float y, float z, String recipe) {
        EntityRef result = entitySpawner.spawnEntityFromRecipe(recipe);

        LocationComponent location = result.createComponent(LocationComponent.class);
        location.setX(x);
        location.setY(y);
        location.setZ(z);

        if (result.hasComponent(BaseLocationComponent.class)) {
            BaseLocationComponent baseLocation = result.getComponent(BaseLocationComponent.class);
            baseLocation.setX(x);
            baseLocation.setY(y);
            baseLocation.setZ(z);
        }

        result.saveChanges();

        return result;
    }

    @Override
    public EntityRef createEntityAt(float x, float y, float z, String prefabName, Map<String, Object> changes) {
        Map<String, Object> entityDef = new HashMap<>(changes);

        Map<String, Object> locationParams = new HashMap<>();
        locationParams.put("x", x);
        locationParams.put("y", y);
        locationParams.put("z", z);

        entityDef.put("+LocationComponent", locationParams);
        entityDef.put("?BaseLocationComponent", locationParams);

        return entitySpawner.spawnEntity(prefabName, entityDef);
    }
}
