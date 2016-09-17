package jgd.platformer.gameplay.level;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.health.PlayerDeath;

@RegisterSystem(profiles = {"gameScreen", "gameplay"})
public class PlayerDeathBoundsCheckSystem implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex levelEntities;
    private EntityIndex playerEntities;

    @Override
    public void initialize() {
        levelEntities = entityIndexManager.addIndexOnComponents(LevelComponent.class);
        playerEntities = entityIndexManager.addIndexOnComponents(PlayerComponent.class, Location3DComponent.class);
    }

    @ReceiveEvent
    public void checkForPlayerOutOfBounds(GameLoopUpdate event, EntityRef entityRef) {
        for (EntityRef playerEntity : playerEntities.getEntities()) {
            ShouldDeathBoundsCheck check = new ShouldDeathBoundsCheck();
            playerEntity.send(check);
            if (!check.isCancelled()) {
                for (EntityRef levelEntity : levelEntities.getEntities()) {
                    Vector3 location = playerEntity.getComponent(Location3DComponent.class).getLocation();
                    float playerX = location.x;
                    float playerY = location.y;

                    LevelComponent level = levelEntity.getComponent(LevelComponent.class);
                    if (level.getMaxX() < playerX
                            || level.getMinX() > playerX
                            || level.getMinY() > playerY) {
                        playerEntity.send(new PlayerDeath());
                    }
                }
            }
        }
    }
}
