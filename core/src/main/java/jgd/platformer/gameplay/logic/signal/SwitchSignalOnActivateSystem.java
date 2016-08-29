package jgd.platformer.gameplay.logic.signal;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.activate.ActivateEntity;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class SwitchSignalOnActivateSystem {
    @Inject
    private SignalManager signalManager;

    @ReceiveEvent
    public void activateEntity(ActivateEntity entity, EntityRef entityRef, SwitchSignalOnActivateComponent switchSignalOnActivate, SignalProducerComponent signalProducer) {
        if (signalManager.isProducing(entityRef)) {
            signalManager.signalDeactivated(entityRef);
        } else {
            signalManager.signalActivated(entityRef);
        }
    }
}
