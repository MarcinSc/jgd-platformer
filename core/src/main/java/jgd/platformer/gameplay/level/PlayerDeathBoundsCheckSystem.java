package jgd.platformer.gameplay.level;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.PlayerDeath;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.logic.PlayerComponent;

@RegisterSystem(profiles = "gameplay")
public class PlayerDeathBoundsCheckSystem implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex deathBoundsEntities;
    private EntityIndex playerEntities;

    @Override
    public void initialize() {
        deathBoundsEntities = entityIndexManager.addIndexOnComponents(PlayerDeathBoundsComponent.class);
        playerEntities = entityIndexManager.addIndexOnComponents(PlayerComponent.class, LocationComponent.class);
    }

    @ReceiveEvent
    public void checkForPlayerOutOfBounds(GameLoopUpdate event, EntityRef entityRef) {
        for (EntityRef playerEntity : playerEntities.getEntities()) {
            for (EntityRef deathBounds : deathBoundsEntities.getEntities()) {
                LocationComponent location = playerEntity.getComponent(LocationComponent.class);
                float playerX = location.getX();
                float playerY = location.getY();

                PlayerDeathBoundsComponent deathBound = deathBounds.getComponent(PlayerDeathBoundsComponent.class);
                if (deathBound.getMaxX() < playerX
                        || deathBound.getMinX() > playerX
                        || deathBound.getMinY() > playerY) {
                    playerEntity.send(new PlayerDeath());
                }
            }
        }
    }
}
