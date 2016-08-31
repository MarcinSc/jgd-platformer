package com.gempukku.gaming.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gempukku.gaming.rendering.event.*;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import org.lwjgl.opengl.Display;

@RegisterSystem(
        profiles = "fivePhaseRenderer",
        shared = {RenderingEngine.class})
public class FivePhaseMasterRenderer implements RenderingEngine, LifeCycleSystem {
    @Inject
    private RenderingEntityProvider renderingEntityProvider;

    private PerspectiveCamera camera;

    private RenderPipelineImpl renderPipeline;
    private CopyShaderProvider copyShaderProvider;
    private ModelInstance copyModelInstance;
    private Model copyModel;
    private ModelBatch copyModelBatch;

    @Override
    public void preInitialize() {
        updateCamera(Display.getWidth(), Display.getHeight());
        renderPipeline = new RenderPipelineImpl();
        copyShaderProvider = new CopyShaderProvider();

        copyModelBatch = new ModelBatch(copyShaderProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("screen", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, new Material());
        backgroundBuilder.rect(
                -1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                1, 1, 1,
                0, 0, 1);
        copyModel = modelBuilder.end();

        copyModelInstance = new ModelInstance(copyModel);
    }

    @Override
    public void postDestroy() {
        copyModel.dispose();
        copyModelBatch.dispose();
        renderPipeline.cleanup();
    }

    @Override
    public void screenResized(int width, int height) {
        updateCamera(width, height);
        renderingEntityProvider.getRenderingEntity().send(new ScreenResized(width, height));
    }

    private void updateCamera(int width, int height) {
        camera = new PerspectiveCamera(75, width, height);
    }

    @Override
    public void render() {
        //noinspection unchecked
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();

        if (renderingEntity != null) {
            setupRenderingCamera();

            FrameBuffer drawFrameBuffer = renderPipeline.getNewFrameBuffer(Display.getWidth(), Display.getHeight(), true, true);
            try {
                renderPipeline.setCurrentBuffer(drawFrameBuffer);

                renderCameraView(renderingEntity, drawFrameBuffer);

                postProcess(renderingEntity);

                renderUi(renderingEntity);

                postUiProcess(renderingEntity);

                renderToScreen();

                renderPipeline.returnFrameBuffer(renderPipeline.getCurrentBuffer());
            } finally {
                renderPipeline.ageOutBuffers();
            }
        } else {
            cleanBuffer();
        }
    }

    private void renderToScreen() {
        copyShaderProvider.setSourceTextureIndex(0);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, renderPipeline.getCurrentBuffer().getColorBufferTexture().getTextureObjectHandle());

        cleanBuffer();

        copyModelBatch.begin(camera);
        copyModelBatch.render(copyModelInstance);
        copyModelBatch.end();
    }

    private void renderUi(EntityRef renderingEntity) {
        renderingEntity.send(new UiRendering(renderPipeline, camera));
    }

    private void setupRenderingCamera() {
        renderingEntityProvider.setupRenderingCamera(camera);
        camera.update();
    }

    private void postProcess(EntityRef renderingEntity) {
        renderingEntity.send(new PostProcessRendering(renderPipeline, camera));
    }

    private void postUiProcess(EntityRef renderingEntity) {
        renderingEntity.send(new PostUiProcessRendering(renderPipeline, camera));
    }

    private void renderCameraView(EntityRef renderingEntity, FrameBuffer drawFrameBuffer) {
        drawFrameBuffer.begin();
        cleanBuffer();
        renderBackdrop(renderingEntity);
        normalRenderPass(renderingEntity);
        renderPostEnvironment(renderingEntity);
        drawFrameBuffer.end();
    }

    private void cleanBuffer() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void renderBackdrop(EntityRef renderingEntity) {
        renderingEntity.send(new RenderBackdrop(renderPipeline, camera));
    }

    private void normalRenderPass(EntityRef renderingEntity) {
        renderingEntity.send(new RenderEnvironment(renderPipeline, renderingEntityProvider.getEnvironment(), camera));
    }

    private void renderPostEnvironment(EntityRef renderingEntity) {
        renderingEntity.send(new PostRenderEnvironment(renderPipeline, camera));
    }
}
