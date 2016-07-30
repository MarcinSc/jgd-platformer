package jgd.platformer.gameplay.logic.ai.combat;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.gaming.time.delay.DelayManager;
import com.gempukku.gaming.time.delay.DelayedActionTriggeredEvent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.logic.ai.FacingDirectionComponent;
import jgd.platformer.gameplay.logic.physics.KineticObjectComponent;

@RegisterSystem(
        profiles = "gameplay"
)
public class CombatSystem {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private DelayManager delayManager;
    @Inject
    private TimeManager timeManager;

    @ReceiveEvent
    public void performProjectileAttack(PerformAttack event, EntityRef entityRef, AttackProjectileComponent attackProjectile, LocationComponent location) {
        long time = timeManager.getTime();
        long lastProjectileShot = attackProjectile.getLastProjectileShot();
        if (lastProjectileShot + attackProjectile.getProjectileShootFrequency() < time) {
            shootProjectile(entityRef, attackProjectile, location);

            attackProjectile.setLastProjectileShot(time);
            entityRef.saveChanges();

            event.succeed();
        }
    }

    private void shootProjectile(EntityRef entityRef, AttackProjectileComponent attackProjectile, LocationComponent location) {
        EntityData projectilePrefab = prefabManager.getPrefabByName(attackProjectile.getProjectilePrefab());
        EntityRef projectile = entityManager.createEntity(projectilePrefab);

        LocationComponent projectileLocation = projectile.createComponent(LocationComponent.class);
        float distanceX = attackProjectile.getDistanceX();
        if (entityRef.hasComponent(FacingDirectionComponent.class)) {
            String entityDirection = entityRef.getComponent(FacingDirectionComponent.class).getDirection();
            float projectileSpeed = attackProjectile.getProjectileSpeed();
            if (entityDirection.equals("left")) {
                distanceX = -distanceX;
                projectileSpeed = -projectileSpeed;
            }
            if (projectile.hasComponent(FacingDirectionComponent.class)) {
                projectile.getComponent(FacingDirectionComponent.class).setDirection(entityDirection);
            }
            if (entityRef.hasComponent(KineticObjectComponent.class)) {
                KineticObjectComponent kineticObject = projectile.getComponent(KineticObjectComponent.class);
                kineticObject.setVelocityX(projectileSpeed);
            }
        }
        projectileLocation.setX(distanceX + location.getX());
        projectileLocation.setY(attackProjectile.getDistanceY() + location.getY());
        projectileLocation.setZ(location.getZ());


        projectile.saveChanges();

        delayManager.addDelayedAction(projectile, "dissipateProjectile", attackProjectile.getDissipateDuration());
    }

    @ReceiveEvent
    public void dissipateProjectile(DelayedActionTriggeredEvent event, EntityRef entityRef) {
        if (event.getActionId().equals("dissipateProjectile")) {
            entityManager.destroyEntity(entityRef);
        }
    }
}
