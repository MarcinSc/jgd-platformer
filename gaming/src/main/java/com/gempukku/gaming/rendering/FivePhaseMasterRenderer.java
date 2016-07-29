package com.gempukku.gaming.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
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
import com.gempukku.gaming.rendering.event.RenderEnvironmentForLight;
import com.gempukku.gaming.rendering.event.UiRendering;
import com.gempukku.gaming.rendering.lighting.DirectionalLightProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import org.lwjgl.opengl.Display;

@RegisterSystem(
        profiles = "fivePhaseRenderer",
        shared = {RenderingEngine.class})
public class FivePhaseMasterRenderer implements RenderingEngine, LifeCycleSystem {
    @Inject(optional = true)
    private DirectionalLightProvider directionalLightProvider;
    @Inject
    private RenderingEntityProvider renderingEntityProvider;

    private PerspectiveCamera camera;
    private FrameBuffer lightFrameBuffer;
    private Camera lightCamera;

    private static int shadowFidelity = 4;

    private RenderPipelineImpl renderPipeline;
    private CopyShaderProvider copyShaderProvider;
    private ModelInstance copyModelInstance;
    private Model copyModel;
    private ModelBatch copyModelBatch;

    @Override
    public void preInitialize() {
        updateCameraAndBuffers(Display.getWidth(), Display.getHeight());
        lightFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, shadowFidelity * 1024, shadowFidelity * 1024, true);
        lightCamera = new PerspectiveCamera(120f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
        lightFrameBuffer.dispose();
        copyModel.dispose();
        copyModelBatch.dispose();
        renderPipeline.dispose();
    }

    @Override
    public void screenResized(int width, int height) {
        updateCameraAndBuffers(width, height);
        renderingEntityProvider.getRenderingEntity().send(new ScreenResized(width, height));
    }

    private void updateCameraAndBuffers(int width, int height) {
        camera = new PerspectiveCamera(75, width, height);
        if (renderPipeline == null) {
            renderPipeline = new RenderPipelineImpl(width, height);
        } else {
            renderPipeline.resetSize(width, height);
        }
    }

    @Override
    public void render() {
        //noinspection unchecked
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();

        if (renderingEntity != null) {
            setupRenderingCamera();

            boolean hasDirectionalLight = setupDirectionalLight();
            if (hasDirectionalLight) {
                renderLightMap(renderingEntity);
            }

            FrameBuffer drawFrameBuffer = renderPipeline.startPipeline();
            try {
                float ambientLight = hasDirectionalLight ? directionalLightProvider.getAmbientLight() : 1f;
                renderCameraView(renderingEntity, drawFrameBuffer, hasDirectionalLight, ambientLight);

                postProcess(renderingEntity);

                renderUi(renderingEntity);

                postUiProcess(renderingEntity);

                renderToScreen();
            } finally {
                renderPipeline.finishPipeline();
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

    private void renderCameraView(EntityRef renderingEntity, FrameBuffer drawFrameBuffer, boolean hasDirectionalLight, float ambientLight) {
        drawFrameBuffer.begin();
        cleanBuffer();
        renderBackdrop(renderingEntity);
        normalRenderPass(renderingEntity, hasDirectionalLight, ambientLight);
        renderPostEnvironment(renderingEntity);
        drawFrameBuffer.end();
    }

    private void renderLightMap(EntityRef renderingEntity) {
        lightFrameBuffer.begin();
        cleanBuffer();
        lightRenderPass(renderingEntity);
        lightFrameBuffer.end();
    }

    private void cleanBuffer() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void renderBackdrop(EntityRef renderingEntity) {
        renderingEntity.send(new RenderBackdrop(camera));
    }

    private boolean setupDirectionalLight() {
        if (directionalLightProvider == null)
            return false;

        directionalLightProvider.setupLightCamera(lightCamera);
        lightCamera.update();
        return true;
    }

    private void lightRenderPass(EntityRef renderingEntity) {
        renderingEntity.send(new RenderEnvironmentForLight(lightCamera));
    }

    private void normalRenderPass(EntityRef renderingEntity, boolean hasDirectionalLight, float ambientLight) {
        Texture lightTexture = lightFrameBuffer.getColorBufferTexture();
        renderingEntity.send(new RenderEnvironment(renderingEntityProvider.getEnvironment(), hasDirectionalLight, camera, lightCamera, lightTexture, shadowFidelity, ambientLight));
    }

    private void renderPostEnvironment(EntityRef renderingEntity) {
        renderingEntity.send(new PostRenderEnvironment(camera));
    }
}
