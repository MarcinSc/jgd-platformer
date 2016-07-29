package com.gempukku.gaming.rendering.postprocess;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public interface RenderPipeline {
    FrameBuffer getCurrentBuffer();

    FrameBuffer borrowFrameBuffer();

    void finishPostProcess(FrameBuffer resultBuffer);
}
