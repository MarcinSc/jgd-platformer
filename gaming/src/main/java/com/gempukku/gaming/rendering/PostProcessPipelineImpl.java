package com.gempukku.gaming.rendering;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gempukku.gaming.rendering.postprocess.PostProcessPipeline;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PostProcessPipelineImpl implements PostProcessPipeline {
    private int width;
    private int height;

    private FrameBuffer mainBuffer;

    private List<FrameBuffer> freeFrameBuffers = new LinkedList<>();
    private List<FrameBuffer> borrowedFrameBuffers = new LinkedList<>();

    public PostProcessPipelineImpl(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void resetSize(int width, int height) {
        this.width = width;
        this.height = height;

        cleanUpFreeFrameBuffers();
    }

    private void cleanUpFreeFrameBuffers() {
        for (FrameBuffer freeFrameBuffer : freeFrameBuffers) {
            freeFrameBuffer.dispose();
        }
        freeFrameBuffers.clear();
    }

    @Override
    public FrameBuffer borrowFrameBuffer() {
        if (freeFrameBuffers.isEmpty()) {
            FrameBuffer newFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
            borrowedFrameBuffers.add(newFrameBuffer);
            return newFrameBuffer;
        } else {
            FrameBuffer availableFrameBuffer = freeFrameBuffers.remove(0);
            borrowedFrameBuffers.add(availableFrameBuffer);
            return availableFrameBuffer;
        }
    }

    @Override
    public void finishPostProcess(FrameBuffer resultBuffer) {
        mainBuffer = resultBuffer;
        Iterator<FrameBuffer> iterator = borrowedFrameBuffers.iterator();
        while (iterator.hasNext()) {
            FrameBuffer borrowed = iterator.next();
            if (borrowed != resultBuffer) {
                freeFrameBuffers.add(borrowed);
                iterator.remove();
            }
        }
    }

    public FrameBuffer getSourceBuffer() {
        return mainBuffer;
    }

    public FrameBuffer startPipeline() {
        mainBuffer = borrowFrameBuffer();
        return mainBuffer;
    }

    public void finishPipeline() {
        freeFrameBuffers.addAll(borrowedFrameBuffers);
        borrowedFrameBuffers.clear();
        mainBuffer = null;
    }

    public void dispose() {

    }
}
