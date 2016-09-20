package jgd.platformer.gameplay.level;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        profiles = {"gameScreen"},
        shared = LevelLoader.class
)
public class LevelSystem implements LevelLoader {
    @Inject
    private EntityManager entityManager;
    @Inject
    private PrefabManager prefabManager;

    private EntityRef levelEntity;

    @Override
    public void loadLevel(String levelPrefabName) {
        EntityData levelData = prefabManager.getPrefabByName(levelPrefabName);
        loadLevel(levelData);
    }

    @Override
    public void loadLevel(EntityData entityData) {
        levelEntity = entityManager.createEntity(entityData);
        levelEntity.send(new AfterLevelLoaded());
    }

    @Override
    public void unloadLevel() {
        levelEntity.send(new BeforeLevelUnloaded());
        entityManager.destroyEntity(levelEntity);
        levelEntity = null;

        for (EntityRef entityRef : entityManager.getAllEntities()) {
            if (!entityRef.hasComponent(KeepWithoutLevelComponent.class))
                entityManager.destroyEntity(entityRef);
        }
    }

    @Override
    public void createNewLevel() {
        EntityData levelData = prefabManager.getPrefabByName("emptyLevel");
        levelEntity = entityManager.createEntity(levelData);
        levelEntity.send(new AfterLevelLoaded());
    }
}
