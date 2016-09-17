package com.gempukku.gaming.rendering.postprocess.tint.color;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface ColorTintComponent extends Component {
    Color getColor();

    void setColor(Color color);

    float getFactor();
    void setFactor(float factor);

    boolean isPostUi();
    void setPostUi(boolean postUi);
}
