package jgd.platformer.level;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import jgd.platformer.component.LocationComponent;
import jgd.platformer.logic.PlayerComponent;

@RegisterSystem
public class PlayerSpawnSystem {
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void levelLoaded(AfterComponentAdded event, EntityRef entity, LevelComponent level, PlayerSpawnComponent playerSpawn) {
        for (EntityRef playerEntity : entityManager.getEntitiesWithComponents(PlayerComponent.class, LocationComponent.class)) {
            LocationComponent location = playerEntity.getComponent(LocationComponent.class);
            location.setX(playerSpawn.getX());
            location.setY(playerSpawn.getY());
            location.setZ(playerSpawn.getZ());
            playerEntity.saveChanges();
        }
    }
}
