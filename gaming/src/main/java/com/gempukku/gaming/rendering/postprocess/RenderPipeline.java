package com.gempukku.gaming.rendering.postprocess;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public interface RenderPipeline {
    FrameBuffer getCurrentBuffer();

    void setCurrentBuffer(FrameBuffer frameBuffer);

    FrameBuffer getNewFrameBuffer(int width, int height, boolean depth, boolean stencil);

    void returnFrameBuffer(FrameBuffer frameBuffer);
}
