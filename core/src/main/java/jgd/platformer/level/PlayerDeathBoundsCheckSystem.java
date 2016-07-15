package jgd.platformer.level;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameLoop;
import com.gempukku.secsy.entity.game.GameLoopListener;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.component.LocationComponent;
import jgd.platformer.logic.PlayerComponent;

@RegisterSystem(profiles = "gameplay")
public class PlayerDeathBoundsCheckSystem implements LifeCycleSystem, GameLoopListener {
    @Inject
    private GameLoop gameLoop;
    @Inject
    private EntityIndexManager entityIndexManager;
    private EntityIndex deathBoundsEntities;
    private EntityIndex playerEntities;


    @Override
    public void initialize() {
        gameLoop.addGameLoopListener(this);

        deathBoundsEntities = entityIndexManager.addIndexOnComponents(PlayerDeathBoundsComponent.class);
        playerEntities = entityIndexManager.addIndexOnComponents(PlayerComponent.class, LocationComponent.class);
    }

    @Override
    public void update() {
        for (EntityRef playerEntity : playerEntities.getEntities()) {
            for (EntityRef deathBounds : deathBoundsEntities.getEntities()) {
                LocationComponent location = playerEntity.getComponent(LocationComponent.class);
                float playerX = location.getX();
                float playerY = location.getY();

                PlayerDeathBoundsComponent deathBound = deathBounds.getComponent(PlayerDeathBoundsComponent.class);
                if (deathBound.getMaxX() < playerX
                        || deathBound.getMinX() > playerX
                        || deathBound.getMinY() > playerY) {
                    System.out.println("Player is dead");
                }
            }
        }
    }
}
