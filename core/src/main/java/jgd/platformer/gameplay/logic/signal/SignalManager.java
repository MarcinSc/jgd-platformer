package jgd.platformer.gameplay.logic.signal;

import com.gempukku.secsy.entity.EntityRef;

public interface SignalManager {
    void signalActivated(EntityRef entityRef);

    void signalDeactivated(EntityRef entityRef);

    boolean isProducing(EntityRef entityRef);
}
