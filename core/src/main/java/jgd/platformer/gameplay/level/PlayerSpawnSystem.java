package jgd.platformer.gameplay.level;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.PlayerComponent;

@RegisterSystem(profiles = {"gameScreen", "gameplay"})
public class PlayerSpawnSystem {
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent(priority = 0)
    public void levelLoaded(AfterLevelLoaded event, EntityRef entity) {
        EntityRef playerSpawn = entityManager.getEntitiesWithComponents(PlayerSpawnComponent.class, Location3DComponent.class).iterator().next();
        Vector3 spawnLocation = playerSpawn.getComponent(Location3DComponent.class).getLocation();

        for (EntityRef playerEntity : entityManager.getEntitiesWithComponents(PlayerComponent.class, Location3DComponent.class)) {
            Location3DComponent locationComp = playerEntity.getComponent(Location3DComponent.class);
            locationComp.setLocation(spawnLocation);
            playerEntity.saveChanges();
        }
    }
}
