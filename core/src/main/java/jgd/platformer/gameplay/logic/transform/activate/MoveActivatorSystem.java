package jgd.platformer.gameplay.logic.transform.activate;

import com.gempukku.gaming.time.TimeManager;
import com.gempukku.gaming.time.delay.DelayManager;
import com.gempukku.gaming.time.delay.DelayedActionTriggeredEvent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.activate.ActivateEntity;
import jgd.platformer.gameplay.logic.physics.ShouldProcessPhysics;
import jgd.platformer.gameplay.logic.transform.ModelTranslateOverTimeComponent;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class MoveActivatorSystem {
    @Inject
    private TimeManager timeManager;
    @Inject
    private DelayManager delayManager;

    @ReceiveEvent
    public void moveActivator(ActivateEntity event, EntityRef entityRef, MoveActivatorComponent moveActivator) {
        EntityRef activator = event.getActivator();
        Location3DComponent activatorLocation = activator.getComponent(Location3DComponent.class);

        ModelTranslateOverTimeComponent modelTranslateOverTime = activator.createComponent(ModelTranslateOverTimeComponent.class);
        modelTranslateOverTime.setSource(activatorLocation.getLocation());
        modelTranslateOverTime.setDestination(activatorLocation.getLocation().add(moveActivator.getDistance()));

        modelTranslateOverTime.setStartTime(timeManager.getTime());
        long moveTime = moveActivator.getMoveTime();
        modelTranslateOverTime.setMoveTime(moveTime);

        activator.createComponent(ActivatorMovedComponent.class);

        activator.saveChanges();

        delayManager.addDelayedAction(activator, "MoveActivatorSystem:removeComponent", moveTime);
    }

    @ReceiveEvent
    public void removeComponent(DelayedActionTriggeredEvent event, EntityRef entityRef, ActivatorMovedComponent activatorMoved) {
        entityRef.removeComponents(ActivatorMovedComponent.class);
        entityRef.saveChanges();
    }

    @ReceiveEvent
    public void preventPhysicsProcessing(ShouldProcessPhysics event, EntityRef entityRef, ActivatorMovedComponent activatorMoved) {
        event.cancel();
    }
}
