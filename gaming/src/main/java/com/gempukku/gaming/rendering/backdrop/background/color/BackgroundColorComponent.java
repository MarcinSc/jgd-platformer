package com.gempukku.gaming.rendering.backdrop.background.color;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface BackgroundColorComponent extends Component {
    Color getColor();

    void setColor(Color color);
}
