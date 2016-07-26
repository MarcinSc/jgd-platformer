package jgd.platformer.gameplay.logic.spawning;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapEvent;

@RegisterSystem
public class SpawnEntitySystem {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void hitboxHit(HitboxOverlapEvent event, EntityRef entityRef, SpawnEntityOnOverlapComponent spawnEntity, LocationComponent spawnerLocation) {
        EntityRef otherEntity = event.getOtherEntity();
        if (otherEntity.hasComponent(PlayerComponent.class)) {
            EntityRef spawnedEntity = entityManager.createEntity(prefabManager.getPrefabByName(spawnEntity.getPrefabName()));
            LocationComponent location = spawnedEntity.createComponent(LocationComponent.class);
            location.setX(spawnerLocation.getX() + spawnEntity.getDistanceX());
            location.setY(spawnerLocation.getY() + spawnEntity.getDistanceY());
            location.setZ(spawnerLocation.getZ() + spawnEntity.getDistanceZ());
            spawnedEntity.saveChanges();

            entityRef.removeComponents(SpawnEntityOnOverlapComponent.class);
            entityRef.saveChanges();
        }
    }
}
