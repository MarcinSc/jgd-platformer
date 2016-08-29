package jgd.platformer.gameplay.logic.signal.consumer.translate;

import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.BaseLocationComponent;
import jgd.platformer.gameplay.component.LocationComponent;
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
    public void moveEntity(SignalActivated event, EntityRef entityRef, ModelTranslateOnSignalComponent translateOnSignal, BaseLocationComponent baseLocation, LocationComponent location) {
        long time = timeManager.getTime();
        ModelTranslateOverTimeComponent translateOverTime = entityRef.getComponent(ModelTranslateOverTimeComponent.class);
        if (translateOverTime == null)
            translateOverTime = entityRef.createComponent(ModelTranslateOverTimeComponent.class);

        translateOverTime.setSourceX(location.getX());
        translateOverTime.setSourceY(location.getY());
        translateOverTime.setSourceZ(location.getZ());

        translateOverTime.setDestinationX(baseLocation.getX() + translateOnSignal.getDistanceX());
        translateOverTime.setDestinationY(baseLocation.getY() + translateOnSignal.getDistanceY());
        translateOverTime.setDestinationZ(baseLocation.getZ() + translateOnSignal.getDistanceZ());

        translateOverTime.setStartTime(time);
        translateOverTime.setMoveTime(translateOnSignal.getMoveTime());

        translateOverTime.setInterpolation(translateOnSignal.getInterpolation());

        entityRef.saveChanges();
    }

    @ReceiveEvent
    public void moveEntityBack(SignalDeactivated event, EntityRef entityRef, ModelTranslateOnSignalComponent translateOnSignal, BaseLocationComponent baseLocation, LocationComponent location) {
        long time = timeManager.getTime();
        ModelTranslateOverTimeComponent translateOverTime = entityRef.getComponent(ModelTranslateOverTimeComponent.class);
        if (translateOverTime == null)
            translateOverTime = entityRef.createComponent(ModelTranslateOverTimeComponent.class);

        translateOverTime.setSourceX(location.getX());
        translateOverTime.setSourceY(location.getY());
        translateOverTime.setSourceZ(location.getZ());

        translateOverTime.setDestinationX(baseLocation.getX());
        translateOverTime.setDestinationY(baseLocation.getY());
        translateOverTime.setDestinationZ(baseLocation.getZ());

        translateOverTime.setStartTime(time);
        translateOverTime.setMoveTime(translateOnSignal.getMoveTime());

        translateOverTime.setInterpolation(translateOnSignal.getInterpolation());

        entityRef.saveChanges();
        entityRef.saveChanges();
    }
}
