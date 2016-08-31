package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FixedFrameBuffer extends FrameBuffer {
    public FixedFrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth) {
        super(format, width, height, hasDepth);
    }

    public FixedFrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        super(format, width, height, hasDepth, hasStencil);
    }

    @Override
    public int getDepthStencilPackedBuffer() {
        return super.getDepthStencilPackedBuffer();
    }
}
