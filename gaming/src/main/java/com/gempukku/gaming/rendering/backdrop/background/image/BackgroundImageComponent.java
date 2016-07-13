package com.gempukku.gaming.rendering.backdrop.background.image;

import com.gempukku.secsy.entity.Component;

public interface BackgroundImageComponent extends Component {
    String getTextureAtlasId();

    String getTextureName();

    float getPaddingTop();

    float getPaddingBottom();

    float getPaddingLeft();

    float getPaddingRight();

    int getBackgroundRed();

    int getBackgroundGreen();

    int getBackgroundBlue();
}
