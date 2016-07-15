package jgd.platformer.player;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        profiles = "gameplay",
        shared = PlayerManager.class
)
public class PlayerSystem implements PlayerManager {
    @Inject
    private EntityManager entityManager;
    @Inject
    private PrefabManager prefabManager;

    @Override
    public void createPlayer() {
        EntityData playerPrefab = prefabManager.getPrefabByName("player");
        entityManager.createEntity(playerPrefab);
    }
}
