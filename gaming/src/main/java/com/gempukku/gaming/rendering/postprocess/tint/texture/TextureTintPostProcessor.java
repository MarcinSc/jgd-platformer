package com.gempukku.gaming.rendering.postprocess.tint.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.rendering.event.PostProcessRendering;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem
public class TextureTintPostProcessor implements LifeCycleSystem {
    @Inject(optional = true)
    private TextureAtlasProvider textureAtlasProvider;

    private ModelBatch modelBatch;

    private TextureTintShaderProvider tintShaderProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void preInitialize() {
        tintShaderProvider = new TextureTintShaderProvider();

        modelBatch = new ModelBatch(tintShaderProvider);
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

    @ReceiveEvent(priorityName = "gaming.renderer.tint.texture")
    public void render(PostProcessRendering event, EntityRef renderingEntity, TextureTintComponent tint) {
        float factor = tint.getFactor();

        if (factor > 0) {
            RenderPipeline renderPipeline = event.getRenderPipeline();

            tintShaderProvider.setFactor(factor);

            setupTintTexture(tint);

            setupSourceTexture(renderPipeline);

            FrameBuffer frameBuffer = renderPipeline.borrowFrameBuffer();
            frameBuffer.begin();

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            modelBatch.begin(event.getCamera());
            modelBatch.render(modelInstance);
            modelBatch.end();

            frameBuffer.end();
            renderPipeline.finishPostProcess(frameBuffer);
        }
    }

    private void setupSourceTexture(RenderPipeline renderPipeline) {
        tintShaderProvider.setSourceTextureIndex(0);

        int textureHandle = renderPipeline.getCurrentBuffer().getColorBufferTexture().getTextureObjectHandle();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureHandle);
    }

    private void setupTintTexture(TextureTintComponent tint) {
        tintShaderProvider.setTintTextureIndex(1);
        TextureRegion texture = textureAtlasProvider.getTexture(tint.getTextureAtlasId(), tint.getTextureName());

        int tintTextureHandle = texture.getTexture().getTextureObjectHandle();
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, tintTextureHandle);

        tintShaderProvider.setTintTextureOrigin(new Vector2(texture.getU(), texture.getV()));
        tintShaderProvider.setTintTextureSize(new Vector2(texture.getU2() - texture.getU(), texture.getV2() - texture.getV()));
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }

}
