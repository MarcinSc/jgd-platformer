package jgd.platformer.gameplay.logic.signal.consumer.translate;

import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.BaseLocation3DComponent;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.signal.SignalActivated;
import jgd.platformer.gameplay.logic.signal.SignalDeactivated;
import jgd.platformer.gameplay.logic.transform.ModelTranslateOverTimeComponent;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class ModelTranslateOnSignalSystem {
    @Inject
    private TimeManager timeManager;

    @ReceiveEvent
    public void moveEntity(SignalActivated event, EntityRef entityRef, ModelTranslateOnSignalComponent translateOnSignal, BaseLocation3DComponent baseLocation, Location3DComponent location) {
        long time = timeManager.getTime();
        ModelTranslateOverTimeComponent translateOverTime = entityRef.getComponent(ModelTranslateOverTimeComponent.class);
        if (translateOverTime == null)
            translateOverTime = entityRef.createComponent(ModelTranslateOverTimeComponent.class);

        translateOverTime.setSource(location.getLocation());
        translateOverTime.setDestination(baseLocation.getLocation().add(translateOnSignal.getDistance()));

        translateOverTime.setStartTime(time);
        translateOverTime.setMoveTime(translateOnSignal.getMoveTime());

        translateOverTime.setInterpolation(translateOnSignal.getInterpolation());

        entityRef.saveChanges();
    }

    @ReceiveEvent
    public void moveEntityBack(SignalDeactivated event, EntityRef entityRef, ModelTranslateOnSignalComponent translateOnSignal, BaseLocation3DComponent baseLocation, Location3DComponent location) {
        long time = timeManager.getTime();
        ModelTranslateOverTimeComponent translateOverTime = entityRef.getComponent(ModelTranslateOverTimeComponent.class);
        if (translateOverTime == null)
            translateOverTime = entityRef.createComponent(ModelTranslateOverTimeComponent.class);

        translateOverTime.setSource(location.getLocation());
        translateOverTime.setDestination(baseLocation.getLocation());

        translateOverTime.setStartTime(time);
        translateOverTime.setMoveTime(translateOnSignal.getMoveTime());

        translateOverTime.setInterpolation(translateOnSignal.getInterpolation());

        entityRef.saveChanges();
        entityRef.saveChanges();
    }
}
