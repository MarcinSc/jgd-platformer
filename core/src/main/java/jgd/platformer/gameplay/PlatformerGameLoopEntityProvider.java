package jgd.platformer.gameplay;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameLoopEntityProvider;

@RegisterSystem(
        profiles = "gameplay",
        shared = GameLoopEntityProvider.class
)
public class PlatformerGameLoopEntityProvider implements GameLoopEntityProvider, LifeCycleSystem {
    @Inject
    private EntityManager entityManager;

    private EntityRef gameLoopEntity;

    @Override
    public void initialize() {
        gameLoopEntity = entityManager.createEntity();
    }

    @Override
    public EntityRef getGameLoopEntity() {
        return gameLoopEntity;
    }
}
