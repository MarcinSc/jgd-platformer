package com.gempukku.gaming.rendering.backdrop.background.image;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface BackgroundImageComponent extends Component {
    String getTextureAtlasId();
    void setTextureAtlasId(String textureAtlasId);

    String getTextureName();
    void setTextureName(String textureName);

    float getPaddingTop();
    void setPaddingTop(float paddingTop);

    float getPaddingBottom();
    void setPaddingBottom(float paddingBottom);

    float getPaddingLeft();
    void setPaddingLeft(float paddingLeft);

    float getPaddingRight();
    void setPaddingRight(float paddingRight);

    Color getBackgroundColor();

    void setBackgroundColor(Color backgroundColor);
}
