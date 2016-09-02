package com.gempukku.gaming.rendering.postprocess.gamma;

import com.badlogic.gdx.Gdx;
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
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem
public class GammaProcessor implements LifeCycleSystem {
    private ModelBatch modelBatch;

    private GammaShaderProvider gammaShaderProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void preInitialize() {
        gammaShaderProvider = new GammaShaderProvider();

        modelBatch = new ModelBatch(gammaShaderProvider);
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

    @ReceiveEvent(priorityName = "gaming.renderer.gamma")
    public void render(PostProcessRendering event, EntityRef renderingEntity, GammaComponent gamma) {
        float factor = gamma.getFactor();

        if (factor != 1) {
            gammaShaderProvider.setSourceTextureIndex(0);
            gammaShaderProvider.setFactor(factor);

            RenderPipeline renderPipeline = event.getRenderPipeline();

            FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();
            int textureHandle = currentBuffer.getColorBufferTexture().getTextureObjectHandle();

            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureHandle);

            FrameBuffer frameBuffer = renderPipeline.getNewFrameBuffer(currentBuffer.getWidth(), currentBuffer.getHeight(), false);
            frameBuffer.begin();

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            modelBatch.begin(event.getCamera());
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
