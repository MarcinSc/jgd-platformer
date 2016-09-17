package jgd.platformer.gameplay.rendering.model.signal;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface SignalIndicatorComponent extends Component {
    Color getBaseColor();

    Color getSignalOffColor();

    Color getSignalOnColor();
}
