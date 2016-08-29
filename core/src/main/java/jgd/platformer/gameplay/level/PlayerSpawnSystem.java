package jgd.platformer.gameplay.level;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.logic.PlayerComponent;

@RegisterSystem(profiles = {"gameScreen", "gameplay"})
public class PlayerSpawnSystem {
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void levelLoaded(AfterLevelLoaded event, EntityRef entity, LevelComponent level, PlayerSpawnComponent playerSpawn) {
        for (EntityRef playerEntity : entityManager.getEntitiesWithComponents(PlayerComponent.class, LocationComponent.class)) {
            LocationComponent location = playerEntity.getComponent(LocationComponent.class);
            location.setX(playerSpawn.getX());
            location.setY(playerSpawn.getY());
            location.setZ(playerSpawn.getZ());
            playerEntity.saveChanges();
        }
    }
}
