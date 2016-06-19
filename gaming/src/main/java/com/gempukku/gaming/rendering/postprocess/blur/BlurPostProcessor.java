package com.gempukku.gaming.rendering.postprocess.blur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gempukku.gaming.rendering.RenderingBuffer;
import com.gempukku.gaming.rendering.postprocess.PostProcessingRenderer;
import com.gempukku.gaming.rendering.postprocess.PostProcessingRendererRegistry;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.NetProfiles;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;

@RegisterSystem(
        profiles = NetProfiles.CLIENT)
public class BlurPostProcessor implements PostProcessingRenderer, LifeCycleSystem {
    @Inject
    private PostProcessingRendererRegistry postProcessingRendererRegistry;

    private ModelBatch modelBatch;

    private BlurShaderProvider blurShaderProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void preInitialize() {
        blurShaderProvider = new BlurShaderProvider();

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

    @Override
    public void initialize() {
        postProcessingRendererRegistry.registerPostProcessingRenderer(this);
    }

    @Override
    public boolean isEnabled(EntityRef observerEntity) {
        return observerEntity.hasComponent(BlurComponent.class);
    }

    @Override
    public void render(EntityRef observerEntity, RenderingBuffer renderingBuffer, Camera camera,
                       int sourceBoundColorTexture, int sourceBoundDepthTexture) {
        float blurRadius = observerEntity.getComponent(BlurComponent.class).getBlurRadius();

        blurShaderProvider.setSourceTextureIndex(sourceBoundColorTexture);
        blurShaderProvider.setBlurRadius(blurRadius);

        renderingBuffer.begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(modelInstance);
        modelBatch.end();
        renderingBuffer.end();
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }

}
