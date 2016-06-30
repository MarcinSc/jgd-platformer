package jgd.platformer.level;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        shared = LevelLoader.class
)
public class LevelSystem implements LevelLoader {
    @Inject
    private EntityManager entityManager;
    @Inject
    private PrefabManager prefabManager;

    @Override
    public void loadLevel(String levelPrefabName) {
        EntityData levelData = prefabManager.getPrefabByName(levelPrefabName);
        entityManager.createEntity(levelData);
    }
}
