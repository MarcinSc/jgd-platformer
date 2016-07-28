package jgd.platformer.gameplay.level;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.component.LocationComponent;

import java.util.Map;

@RegisterSystem(profiles = "gameplay")
public class LevelEntitySystem {
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
                EntityRef result = entityManager.createEntity(entityData);
                LocationComponent location = result.createComponent(LocationComponent.class);
                String[] locationSplit = locationStr.split(",");
                location.setX(Float.parseFloat(locationSplit[0]));
                location.setY(Float.parseFloat(locationSplit[1]));
                location.setZ(Float.parseFloat(locationSplit[2]));
                result.saveChanges();
            }
        }

        if (level.getObjectCoordinates() != null) {
            for (Map.Entry<String, String> objectCoordinates : level.getObjectCoordinates().entrySet()) {
                String locationStr = objectCoordinates.getKey();
                String prefabName = objectCoordinates.getValue();
                EntityData entityData = prefabManager.getPrefabByName(prefabName);
                EntityRef result = entityManager.createEntity(entityData);
                LocationComponent location = result.createComponent(LocationComponent.class);
                String[] locationSplit = locationStr.split(",");
                location.setX(Float.parseFloat(locationSplit[0]));
                location.setY(Float.parseFloat(locationSplit[1]));
                location.setZ(Float.parseFloat(locationSplit[2]));
                result.saveChanges();
            }
        }

        if (level.getAdditionalObjects() != null) {
            for (String additionalObject : level.getAdditionalObjects()) {
                entityManager.createEntity(prefabManager.getPrefabByName(additionalObject));
            }
        }
    }
}
