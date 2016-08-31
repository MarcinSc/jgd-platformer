package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RenderPipelineImpl implements RenderPipeline {
    private FrameBuffer mainBuffer;

    private List<FixedFrameBuffer> oldFrameBuffers = new LinkedList<>();
    private List<FixedFrameBuffer> newFrameBuffers = new LinkedList<>();

    public void ageOutBuffers() {
        for (FrameBuffer freeFrameBuffer : oldFrameBuffers) {
            freeFrameBuffer.dispose();
        }
        oldFrameBuffers.clear();
        oldFrameBuffers.addAll(newFrameBuffers);
        newFrameBuffers.clear();
    }

    public void cleanup() {
        for (FrameBuffer freeFrameBuffer : oldFrameBuffers) {
            freeFrameBuffer.dispose();
        }
        for (FrameBuffer freeFrameBuffer : newFrameBuffers) {
            freeFrameBuffer.dispose();
        }
        oldFrameBuffers.clear();
        newFrameBuffers.clear();
    }

    @Override
    public void setCurrentBuffer(FrameBuffer frameBuffer) {
        mainBuffer = frameBuffer;
    }

    @Override
    public FrameBuffer getNewFrameBuffer(int width, int height, boolean depth, boolean stencil) {
        FrameBuffer buffer = extractFrameBuffer(width, height, depth, stencil, this.oldFrameBuffers);
        if (buffer != null) return buffer;
        buffer = extractFrameBuffer(width, height, depth, stencil, this.newFrameBuffers);
        if (buffer != null) return buffer;

        return new FixedFrameBuffer(Pixmap.Format.RGBA8888, width, height, depth, stencil);
    }

    private FrameBuffer extractFrameBuffer(int width, int height, boolean depth, boolean stencil, List<FixedFrameBuffer> frameBuffers) {
        Iterator<FixedFrameBuffer> iterator = frameBuffers.iterator();
        while (iterator.hasNext()) {
            FixedFrameBuffer buffer = iterator.next();
            if (buffer.getWidth() == width && buffer.getHeight() == height
                    && depth == (buffer.getDepthBufferHandle() != 0 || buffer.getDepthStencilPackedBuffer() != 0)
                    && stencil == (buffer.getStencilBufferHandle() != 0 || buffer.getDepthStencilPackedBuffer() != 0)) {
                iterator.remove();
                return buffer;
            }
        }
        return null;
    }

    @Override
    public void returnFrameBuffer(FrameBuffer frameBuffer) {
        newFrameBuffers.add((FixedFrameBuffer) frameBuffer);
    }

    public FrameBuffer getCurrentBuffer() {
        return mainBuffer;
    }
}
