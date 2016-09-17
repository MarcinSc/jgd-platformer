package jgd.platformer.gameplay.logic.ai.combat;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.gaming.time.delay.DelayManager;
import com.gempukku.gaming.time.delay.DelayedActionTriggeredEvent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.ai.FacingDirectionComponent;
import jgd.platformer.gameplay.logic.spawning.PlatformerEntitySpawner;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class CombatSystem {
    @Inject
    private PlatformerEntitySpawner platformerEntitySpawner;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private DelayManager delayManager;
    @Inject
    private TimeManager timeManager;

    @ReceiveEvent
    public void performProjectileAttack(PerformAttack event, EntityRef entityRef, AttackProjectileComponent attackProjectile, Location3DComponent location) {
        long time = timeManager.getTime();
        long lastProjectileShot = attackProjectile.getLastProjectileShot();
        if (lastProjectileShot + attackProjectile.getProjectileShootFrequency() < time) {
            shootProjectile(entityRef, attackProjectile, location);

            attackProjectile.setLastProjectileShot(time);
            entityRef.saveChanges();

            event.succeed();
        }
    }

    private void shootProjectile(EntityRef entityRef, AttackProjectileComponent attackProjectile, Location3DComponent locationComp) {
        Vector3 location = locationComp.getLocation();
        Map<String, Object> projectileRecipe = attackProjectile.getProjectileRecipe();
        String prefabName = (String) projectileRecipe.get("prefabName");
        Map<String, Object> changes = (Map<String, Object>) projectileRecipe.get("changes");

        float distanceX = attackProjectile.getDistanceX();
        if (entityRef.hasComponent(FacingDirectionComponent.class)) {
            String entityDirection = entityRef.getComponent(FacingDirectionComponent.class).getDirection();
            float projectileSpeed = attackProjectile.getProjectileSpeed();
            if (entityDirection.equals("left")) {
                distanceX = -distanceX;
                projectileSpeed = -projectileSpeed;
            }

            Map<String, Object> directionValues = new HashMap<>();
            directionValues.put("direction", entityDirection);
            changes.put("?FacingDirectionComponent", directionValues);

            Map<String, Object> kineticValues = new HashMap<>();
            kineticValues.put("velocityX", projectileSpeed);
            changes.put("?KineticObjectComponent", kineticValues);
        }

        float x = distanceX + location.x;
        float y = attackProjectile.getDistanceY() + location.y;
        float z = location.z;

        EntityRef projectile = platformerEntitySpawner.createEntityAt(x, y, z, prefabName, changes);

        delayManager.addDelayedAction(projectile, "dissipateProjectile", attackProjectile.getDissipateDuration());
    }

    @ReceiveEvent
    public void dissipateProjectile(DelayedActionTriggeredEvent event, EntityRef entityRef) {
        if (event.getActionId().equals("dissipateProjectile")) {
            entityManager.destroyEntity(entityRef);
        }
    }
}
