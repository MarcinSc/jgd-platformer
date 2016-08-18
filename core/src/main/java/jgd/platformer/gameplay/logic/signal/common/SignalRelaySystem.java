package jgd.platformer.gameplay.logic.signal.common;

import com.gempukku.gaming.time.delay.DelayManager;
import com.gempukku.gaming.time.delay.DelayedActionTriggeredEvent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.signal.SignalActivated;
import jgd.platformer.gameplay.logic.signal.SignalDeactivated;
import jgd.platformer.gameplay.logic.signal.SignalManager;

@RegisterSystem(
        profiles = "gameplay"
)
public class SignalRelaySystem {
    private static final String SIGNAL_ACTIVATED = "SignalRelay:signalActivated";
    private static final String SIGNAL_DEACTIVATED = "SignalRelay:signalDeactivated";

    @Inject
    private SignalManager signalManager;
    @Inject
    private DelayManager delayManager;

    @ReceiveEvent
    public void signalOn(SignalActivated event, EntityRef entityRef, SignalRelayComponent signalRelay) {
        if (delayManager.hasDelayedAction(entityRef, SIGNAL_DEACTIVATED)) {
            delayManager.cancelDelayedAction(entityRef, SIGNAL_DEACTIVATED);
        } else {
            long onDelay = signalRelay.getOnDelay();
            if (onDelay <= 0) {
                signalManager.signalActivated(entityRef);
            } else {
                delayManager.addDelayedAction(entityRef, SIGNAL_ACTIVATED, onDelay);
            }
        }
    }

    @ReceiveEvent
    public void signalOff(SignalDeactivated event, EntityRef entityRef, SignalRelayComponent signalRelay) {
        if (delayManager.hasDelayedAction(entityRef, SIGNAL_ACTIVATED)) {
            delayManager.cancelDelayedAction(entityRef, SIGNAL_ACTIVATED);
        } else {
            long offDelay = signalRelay.getOffDelay();
            if (offDelay <= 0) {
                signalManager.signalDeactivated(entityRef);
            } else {
                delayManager.addDelayedAction(entityRef, SIGNAL_DEACTIVATED, offDelay);
            }
        }
    }

    @ReceiveEvent
    public void actionTriggered(DelayedActionTriggeredEvent event, EntityRef entityRef, SignalRelayComponent signalRelay) {
        if (event.getActionId().equals(SIGNAL_ACTIVATED)) {
            signalManager.signalActivated(entityRef);
        } else if (event.getActionId().equals(SIGNAL_DEACTIVATED)) {
            signalManager.signalDeactivated(entityRef);
        }
    }
}
