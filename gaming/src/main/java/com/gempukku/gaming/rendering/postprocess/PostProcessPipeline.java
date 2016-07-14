package com.gempukku.gaming.rendering.postprocess;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public interface PostProcessPipeline {
    FrameBuffer getSourceBuffer();

    FrameBuffer borrowFrameBuffer();

    void finishPostProcess(FrameBuffer resultBuffer);
}
