package jgd.platformer.gameplay.logic.signal;

import com.gempukku.secsy.entity.Component;

public interface SignalConsumerComponent extends Component {
    String getChannel();

    boolean isReceivingSignal();

    void setReceivingSignal(boolean receivingSignal);
}
