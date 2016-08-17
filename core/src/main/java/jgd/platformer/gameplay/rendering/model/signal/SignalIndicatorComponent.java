package jgd.platformer.gameplay.rendering.model.signal;

import com.gempukku.secsy.entity.Component;

public interface SignalIndicatorComponent extends Component {
    int getBaseRed();

    int getBaseGreen();

    int getBaseBlue();

    int getSignalOffRed();

    int getSignalOffGreen();

    int getSignalOffBlue();

    int getSignalOnRed();

    int getSignalOnGreen();

    int getSignalOnBlue();
}
