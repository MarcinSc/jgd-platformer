package com.gempukku.gaming.rendering.postprocess.tint.color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gempukku.gaming.rendering.event.PostProcessRendering;
import com.gempukku.gaming.rendering.event.PostUiProcessRendering;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "colorTint")
public class ColorTintPostProcessor implements LifeCycleSystem {
    private ModelBatch modelBatch;

    private ColorTintShaderProvider tintShaderProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void preInitialize() {
        tintShaderProvider = new ColorTintShaderProvider();

        modelBatch = new ModelBatch(tintShaderProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("screen", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, new Material());
        backgroundBuilder.rect(
                0, 1, 1,
                0, 0, 1,
                1, 0, 1,
                1, 1, 1,
                0, 0, 1);
        model = modelBuilder.end();

        modelInstance = new ModelInstance(model);
    }

    @ReceiveEvent(priorityName = "gaming.renderer.tint.preUi.color")
    public void processPreUi(PostProcessRendering event, EntityRef renderingEntity, ColorTintComponent tint) {
        if (!tint.isPostUi())
            postProcess(tint, event.getRenderPipeline(), event.getCamera());
    }

    @ReceiveEvent(priorityName = "gaming.renderer.tint.postUi.color")
    public void processPostUi(PostUiProcessRendering event, EntityRef renderingEntity, ColorTintComponent tint) {
        if (tint.isPostUi())
            postProcess(tint, event.getRenderPipeline(), event.getCamera());
    }

    private void postProcess(ColorTintComponent tint, RenderPipeline renderPipeline, Camera camera) {
        float factor = tint.getFactor();

        if (factor > 0) {
            tintShaderProvider.setSourceTextureIndex(0);
            tintShaderProvider.setFactor(factor);
            tintShaderProvider.setColor(new Color(tint.getRed() / 255f, tint.getGreen() / 255f, tint.getBlue() / 255f, 1f));

            FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();
            int textureHandle = currentBuffer.getColorBufferTexture().getTextureObjectHandle();

            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureHandle);

            FrameBuffer frameBuffer = renderPipeline.getNewFrameBuffer(currentBuffer.getWidth(), currentBuffer.getHeight(), false);
            frameBuffer.begin();

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            modelBatch.begin(camera);
            modelBatch.render(modelInstance);
            modelBatch.end();

            frameBuffer.end();
            renderPipeline.returnFrameBuffer(currentBuffer);
            renderPipeline.setCurrentBuffer(frameBuffer);
        }
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }
}
