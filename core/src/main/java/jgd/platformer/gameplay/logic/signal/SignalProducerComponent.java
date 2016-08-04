package jgd.platformer.gameplay.logic.signal;

import com.gempukku.secsy.entity.Component;

public interface SignalProducerComponent extends Component {
    String getChannel();

    boolean isProducingSignal();

    void setProducingSignal(boolean producingSignal);
}
