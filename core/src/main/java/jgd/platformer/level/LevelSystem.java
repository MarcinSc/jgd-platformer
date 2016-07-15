package jgd.platformer.level;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        profiles = "gameplay",
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
        if (levelEntity != null) {
            entityManager.destroyEntity(levelEntity);
            levelEntity = null;
        }
        EntityData levelData = prefabManager.getPrefabByName(levelPrefabName);
        levelEntity = entityManager.createEntity(levelData);
    }
}
