package com.gempukku.gaming.rendering.postprocess.tint.color;

import com.gempukku.secsy.entity.Component;

public interface ColorTintComponent extends Component {
    int getRed();

    void setRed(int red);

    int getGreen();

    void setGreen(int green);

    int getBlue();

    void setBlue(int blue);

    float getFactor();

    void setFactor(float factor);
}