package jgd.platformer.gameplay.level;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.logic.spawning.PlatformerEntitySpawner;

import java.util.Map;

@RegisterSystem(profiles = {"gameScreen", "gameplay"})
public class LevelEntitySystem {
    @Inject
    private PlatformerEntitySpawner platformerEntitySpawner;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void levelLoaded(AfterLevelLoaded event, EntityRef entity, LevelComponent level) {
        for (Map.Entry<String, String> blockCoordinates : level.getBlockCoordinates().entrySet()) {
            String locationStr = blockCoordinates.getKey();
            String prefabName = blockCoordinates.getValue();
            EntityData entityData = prefabManager.getPrefabByName(prefabName);
            EntityRef blockData = entityManager.wrapEntityData(entityData);
            if (blockData.hasComponent(BlockEntityComponent.class)) {
                String[] locationSplit = locationStr.split(",");
                float x = Float.parseFloat(locationSplit[0]);
                float y = Float.parseFloat(locationSplit[1]);
                float z = Float.parseFloat(locationSplit[2]);
                platformerEntitySpawner.createEntityFromRecipeAt(x, y, z, prefabName);
            }
        }

        if (level.getLocatedObjects() != null) {
            for (Object locatedObject : level.getLocatedObjects()) {
                if (locatedObject instanceof String) {
                    String[] objectSplit = ((String) locatedObject).split("\\|", 2);
                    String locationStr = objectSplit[0];
                    String recipe = objectSplit[1];

                    String[] locationSplit = locationStr.split(",");
                    float x = Float.parseFloat(locationSplit[0]);
                    float y = Float.parseFloat(locationSplit[1]);
                    float z = Float.parseFloat(locationSplit[2]);

                    platformerEntitySpawner.createEntityFromRecipeAt(x, y, z, recipe);
                } else {
                    Map<String, Object> objectMapDef = (Map<String, Object>) locatedObject;
                    String locationStr = (String) objectMapDef.get("location");
                    String prefabName = (String) objectMapDef.get("prefabName");
                    Map<String, Object> changes = (Map<String, Object>) objectMapDef.get("changes");

                    String[] locationSplit = locationStr.split(",");
                    float x = Float.parseFloat(locationSplit[0]);
                    float y = Float.parseFloat(locationSplit[1]);
                    float z = Float.parseFloat(locationSplit[2]);

                    platformerEntitySpawner.createEntityAt(x, y, z, prefabName, changes);
                }
            }
        }

        if (level.getAdditionalObjects() != null) {
            for (Object additionalObject : level.getAdditionalObjects()) {
                if (additionalObject instanceof String) {
                    platformerEntitySpawner.createEntityFromRecipe((String) additionalObject);
                } else {
                    Map<String, Object> objectDef = (Map<String, Object>) additionalObject;
                    platformerEntitySpawner.createEntity((String) objectDef.get("prefabName"), (Map<String, Object>) objectDef.get("changes"));
                }
            }
        }
    }
}
