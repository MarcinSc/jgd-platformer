package com.gempukku.gaming.rendering.postprocess.tint.grain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
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
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.gaming.rendering.event.PostProcessRendering;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.gaming.rendering.postprocess.tint.texture.TextureTintShaderProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

import java.util.Collections;
import java.util.Random;

@RegisterSystem(
        profiles = "grainPostProcessor"
)
public class GrainPostProcessor implements LifeCycleSystem {
    @Inject
    private TextureAtlasProvider textureAtlasProvider;
    @Inject
    private TextureAtlasRegistry textureAtlasRegistry;

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

    @Override
    public void initialize() {
        textureAtlasRegistry.registerTextures("grainPostProcessor", Collections.singleton("image/grain.png"));
    }

    @ReceiveEvent(priorityName = "gaming.renderer.tint.grain")
    public void render(PostProcessRendering event, EntityRef renderingEntity, GrainComponent tint) {
        float factor = tint.getFactor();

        if (factor > 0) {
            RenderPipeline renderPipeline = event.getRenderPipeline();

            Random rnd = new Random();
            tintShaderProvider.setTintShift(new Vector2(rnd.nextFloat(), rnd.nextFloat()));

            tintShaderProvider.setFactor(factor);

            setupTintTexture(event.getCamera(), tint);

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

    private void setupTintTexture(Camera camera, GrainComponent tint) {
        tintShaderProvider.setTintTextureIndex(1);
        TextureRegion texture = textureAtlasProvider.getTexture("grainPostProcessor", "image/grain.png");

        int tintTextureHandle = texture.getTexture().getTextureObjectHandle();
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, tintTextureHandle);

        tintShaderProvider.setTintTextureOrigin(new Vector2(texture.getU(), texture.getV()));
        tintShaderProvider.setTintTextureSize(new Vector2(texture.getU2() - texture.getU(), texture.getV2() - texture.getV()));

        tintShaderProvider.setRepeatFactor(
                new Vector2(
                        camera.viewportWidth / texture.getRegionWidth() / tint.getGrainSize(),
                        camera.viewportHeight / texture.getRegionHeight() / tint.getGrainSize()));
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }

}
