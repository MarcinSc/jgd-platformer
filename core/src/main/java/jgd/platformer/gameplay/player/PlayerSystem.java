package jgd.platformer.gameplay.player;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"},
        shared = PlayerManager.class
)
public class PlayerSystem implements PlayerManager {
    @Inject
    private EntityManager entityManager;
    @Inject
    private PrefabManager prefabManager;

    private EntityRef playerEntity;

    @Override
    public void createPlayer() {
        EntityData playerPrefab = prefabManager.getPrefabByName("player");
        playerEntity = entityManager.createEntity(playerPrefab);
        playerEntity.send(new AfterPlayerCreated());
    }

    @Override
    public void removePlayer() {
        playerEntity.send(new BeforePlayerDestroyed());
        entityManager.destroyEntity(playerEntity);
        playerEntity = null;
    }
}
