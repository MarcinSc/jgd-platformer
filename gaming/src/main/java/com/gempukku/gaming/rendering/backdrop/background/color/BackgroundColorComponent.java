package com.gempukku.gaming.rendering.backdrop.background.color;

import com.gempukku.secsy.entity.Component;

public interface BackgroundColorComponent extends Component {
    int getRed();

    void setRed(int red);

    int getGreen();

    void setGreen(int green);

    int getBlue();

    void setBlue(int blue);
}
