package jgd.platformer.gameplay.logic.spawning;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapEvent;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class SpawnEntitySystem {
    @Inject
    private PlatformerEntitySpawner platformerEntitySpawner;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void hitboxHit(HitboxOverlapEvent event, EntityRef entityRef, SpawnEntityOnOverlapComponent spawnEntity, Location3DComponent spawnerLocationComp) {
        Vector3 spawnerLocation = spawnerLocationComp.getLocation();
        EntityRef otherEntity = event.getOtherEntity();
        if (otherEntity.hasComponent(PlayerComponent.class)) {
            platformerEntitySpawner.createEntityFromRecipeAt(
                    spawnerLocation.x + spawnEntity.getDistanceX(),
                    spawnerLocation.y + spawnEntity.getDistanceY(),
                    spawnerLocation.z + spawnEntity.getDistanceZ(), spawnEntity.getPrefabName());

            entityRef.removeComponents(SpawnEntityOnOverlapComponent.class);
            entityRef.saveChanges();
        }
    }
}
