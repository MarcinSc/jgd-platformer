package jgd.platformer.gameplay.rendering.model;

import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.logic.physics.KineticObjectComponent;

@RegisterSystem(
        profiles = "gameScreen")
public class ModelRotationSystem implements LifeCycleSystem {
    @Inject
    private TimeManager timeManager;
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex rotatingEntities;
    private EntityIndex movementRotationEntities;

    @Override
    public void initialize() {
        rotatingEntities = entityIndexManager.addIndexOnComponents(ConstantModelRotationComponent.class, ModelRotateComponent.class);
        movementRotationEntities = entityIndexManager.addIndexOnComponents(RotateModelOnMovementComponent.class, ModelRotateComponent.class, KineticObjectComponent.class);
    }

    @ReceiveEvent
    public void rotateModels(GameLoopUpdate event, EntityRef entity) {
        float secondsSinceStart = timeManager.getTime() / 1000f;

        for (EntityRef rotatingEntity : rotatingEntities.getEntities()) {
            ConstantModelRotationComponent rotationRate = rotatingEntity.getComponent(ConstantModelRotationComponent.class);
            ModelRotateComponent rotation = rotatingEntity.getComponent(ModelRotateComponent.class);
            rotation.setRotateY(rotationRate.getDegreesPerSecond() * secondsSinceStart);
            rotatingEntity.saveChanges();
        }

        for (EntityRef movementRotationEntity : movementRotationEntities.getEntities()) {
            KineticObjectComponent kineticObject = movementRotationEntity.getComponent(KineticObjectComponent.class);
            RotateModelOnMovementComponent rotateOnMovement = movementRotationEntity.getComponent(RotateModelOnMovementComponent.class);
            ModelRotateComponent rotate = movementRotationEntity.getComponent(ModelRotateComponent.class);

            float velocityX = kineticObject.getVelocityX();
            if (velocityX < 0) {
                rotate.setRotateY(rotateOnMovement.getAngleLeft());
            } else if (velocityX > 0) {
                rotate.setRotateY(rotateOnMovement.getAngleRight());
            }
            movementRotationEntity.saveChanges();
        }
    }
}
