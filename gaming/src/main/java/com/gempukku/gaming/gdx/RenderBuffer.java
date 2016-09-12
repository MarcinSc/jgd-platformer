package com.gempukku.gaming.gdx;

import com.badlogic.gdx.graphics.Texture;

public interface RenderBuffer {
    void begin();

    void end();

    int getWidth();

    int getHeight();

    void setColorBuffer(Texture texture);

    Texture getColorBuffer();

    void setDepthStencilBuffer(Texture texture);

    Texture getDepthStencilBuffer();
}
