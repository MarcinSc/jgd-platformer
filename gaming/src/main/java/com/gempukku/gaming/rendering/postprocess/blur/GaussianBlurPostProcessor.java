package com.gempukku.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gempukku.gaming.rendering.FlipOffScreenRenderingBuffer;
import com.gempukku.gaming.rendering.event.PostProcessRendering;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem
public class GaussianBlurPostProcessor implements LifeCycleSystem {
    private ModelBatch modelBatch;

    private GaussianBlurShaderProvider blurShaderProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void preInitialize() {
        blurShaderProvider = new GaussianBlurShaderProvider();

        modelBatch = new ModelBatch(blurShaderProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("screen", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, new Material());
        backgroundBuilder.rect(
                -1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                1, 1, 1,
                0, 0, 1);
        model = modelBuilder.end();

        modelInstance = new ModelInstance(model);
    }

    @ReceiveEvent
    public void render(PostProcessRendering event, EntityRef renderingEntity, GaussianBlurComponent blur) {
        int blurRadius = blur.getBlurRadius();

        blurShaderProvider.setSourceTextureIndex(0);
        blurShaderProvider.setBlurRadius(blurRadius);

        FlipOffScreenRenderingBuffer renderingBuffer = event.getRenderingBuffer();

        blurShaderProvider.setVertical(true);
        executeBlur(event, renderingBuffer);
        blurShaderProvider.setVertical(false);
        executeBlur(event, renderingBuffer);
    }

    private void executeBlur(PostProcessRendering event, FlipOffScreenRenderingBuffer renderingBuffer) {
        int textureHandle = renderingBuffer.getSourceBuffer().getColorBufferTexture().getTextureObjectHandle();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureHandle);

        renderingBuffer.getDestinationBuffer().begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(event.getCamera());
        modelBatch.render(modelInstance);
        modelBatch.end();

        renderingBuffer.getDestinationBuffer().end();
        renderingBuffer.flip();
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }

}