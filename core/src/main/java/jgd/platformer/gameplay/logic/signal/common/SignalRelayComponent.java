package jgd.platformer.gameplay.logic.signal.common;

import com.gempukku.secsy.entity.Component;

public interface SignalRelayComponent extends Component {
    long getOnDelay();

    long getOffDelay();
}
