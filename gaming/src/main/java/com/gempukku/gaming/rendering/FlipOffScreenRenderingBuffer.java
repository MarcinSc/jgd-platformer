package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FlipOffScreenRenderingBuffer {
    private FrameBuffer firstFrameBuffer;
    private FrameBuffer secondFrameBuffer;
    private boolean drawsToFirst = true;

    public FlipOffScreenRenderingBuffer(FrameBuffer firstFrameBuffer, FrameBuffer secondFrameBuffer) {
        this.firstFrameBuffer = firstFrameBuffer;
        this.secondFrameBuffer = secondFrameBuffer;
    }

    public void flip() {
        drawsToFirst = !drawsToFirst;
    }

    public FrameBuffer getSourceBuffer() {
        if (drawsToFirst)
            return secondFrameBuffer;
        else
            return firstFrameBuffer;
    }

    public FrameBuffer getDestinationBuffer() {
        if (drawsToFirst)
            return firstFrameBuffer;
        else
            return secondFrameBuffer;
    }
}
