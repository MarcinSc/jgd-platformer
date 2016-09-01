package com.gempukku.gaming.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gempukku.gaming.rendering.event.PostProcessRendering;
import com.gempukku.gaming.rendering.event.PostRenderEnvironment;
import com.gempukku.gaming.rendering.event.PostUiProcessRendering;
import com.gempukku.gaming.rendering.event.RenderBackdrop;
import com.gempukku.gaming.rendering.event.RenderEnvironment;
import com.gempukku.gaming.rendering.event.UiRendering;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
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

    private RenderPipelineImpl renderPipeline;
    private CopyShaderProvider copyShaderProvider;
    private ModelInstance copyModelInstance;
    private Model copyModel;
    private ModelBatch copyModelBatch;

    @Override
    public void preInitialize() {
        renderPipeline = new RenderPipelineImpl();
        copyShaderProvider = new CopyShaderProvider();

        copyModelBatch = new ModelBatch(copyShaderProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("screen", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, new Material());
        backgroundBuilder.rect(
                0, 1, 1,
                0, 0, 1,
                1, 0, 1,
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
        renderingEntityProvider.getRenderingEntity().send(new ScreenResized(width, height));
    }

    @Override
    public void render() {
        //noinspection unchecked
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();

        if (renderingEntity != null) {
            int width = Display.getWidth();
            int height = Display.getHeight();

            GetCamera getCamera = new GetCamera(width, height);
            renderingEntity.send(getCamera);

            Camera camera = getCamera.getCamera();

            GetEnvironment getEnvironment = new GetEnvironment(camera);
            renderingEntity.send(getEnvironment);

            Environment environment = getEnvironment.getEnvironment();

            FrameBuffer drawFrameBuffer = renderPipeline.getNewFrameBuffer(width, height, true, true);
            try {
                renderPipeline.setCurrentBuffer(drawFrameBuffer);

                renderEntity(renderPipeline, renderingEntity, camera, environment);

                renderToScreen(camera);

                renderPipeline.returnFrameBuffer(renderPipeline.getCurrentBuffer());
            } finally {
                renderPipeline.ageOutBuffers();
            }
        } else {
            cleanBuffer();
        }
    }

    @Override
    public void renderEntity(RenderPipeline renderPipeline, EntityRef renderingEntity, Camera camera, Environment environment) {
        renderPipeline.getCurrentBuffer().begin();
        cleanBuffer();
        renderPipeline.getCurrentBuffer().end();

        renderBackdrop(renderPipeline, renderingEntity, camera);

        renderEnvironment(renderPipeline, renderingEntity, camera, environment);

        renderPostEnvironment(renderPipeline, renderingEntity, camera);

        postProcess(renderPipeline, renderingEntity, camera);

        renderUi(renderPipeline, renderingEntity, camera);

        postUiProcess(renderPipeline, renderingEntity, camera);
    }

    private void renderToScreen(Camera camera) {
        copyShaderProvider.setSourceTextureIndex(0);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, renderPipeline.getCurrentBuffer().getColorBufferTexture().getTextureObjectHandle());

        cleanBuffer();

        copyModelBatch.begin(camera);
        copyModelBatch.render(copyModelInstance);
        copyModelBatch.end();
    }

    private void renderUi(RenderPipeline renderPipeline, EntityRef renderingEntity, Camera camera) {
        renderingEntity.send(new UiRendering(renderPipeline, camera));
    }

    private void postProcess(RenderPipeline renderPipeline, EntityRef renderingEntity, Camera camera) {
        renderingEntity.send(new PostProcessRendering(renderPipeline, camera));
    }

    private void postUiProcess(RenderPipeline renderPipeline, EntityRef renderingEntity, Camera camera) {
        renderingEntity.send(new PostUiProcessRendering(renderPipeline, camera));
    }

    private void cleanBuffer() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void renderBackdrop(RenderPipeline renderPipeline, EntityRef renderingEntity, Camera camera) {
        renderingEntity.send(new RenderBackdrop(renderPipeline, camera));
    }

    private void renderEnvironment(RenderPipeline renderPipeline, EntityRef renderingEntity, Camera camera, Environment environment) {
        renderingEntity.send(new RenderEnvironment(renderPipeline, environment, camera));
    }

    private void renderPostEnvironment(RenderPipeline renderPipeline, EntityRef renderingEntity, Camera camera) {
        renderingEntity.send(new PostRenderEnvironment(renderPipeline, camera));
    }
}
