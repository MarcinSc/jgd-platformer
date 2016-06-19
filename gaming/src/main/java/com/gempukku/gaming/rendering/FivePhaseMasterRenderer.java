package com.gempukku.gaming.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.gempukku.gaming.rendering.backdrop.BackdropRenderer;
import com.gempukku.gaming.rendering.backdrop.BackdropRendererRegistry;
import com.gempukku.gaming.rendering.environment.EnvironmentRenderer;
import com.gempukku.gaming.rendering.environment.EnvironmentRendererRegistry;
import com.gempukku.gaming.rendering.lighting.DirectionalLightProvider;
import com.gempukku.gaming.rendering.postenvironment.PostEnvironmentRenderer;
import com.gempukku.gaming.rendering.postenvironment.PostEnvironmentRendererRegistry;
import com.gempukku.gaming.rendering.postprocess.PostProcessingRenderer;
import com.gempukku.gaming.rendering.postprocess.PostProcessingRendererRegistry;
import com.gempukku.gaming.rendering.ui.UiRenderer;
import com.gempukku.gaming.rendering.ui.UiRendererRegistry;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.NetProfiles;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.context.util.PriorityCollection;
import com.gempukku.secsy.entity.EntityRef;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@RegisterSystem(
        profiles = NetProfiles.CLIENT,
        shared = {RenderingEngine.class, EnvironmentRendererRegistry.class, UiRendererRegistry.class, BackdropRendererRegistry.class,
                PostEnvironmentRendererRegistry.class, PostProcessingRendererRegistry.class})
public class FivePhaseMasterRenderer implements RenderingEngine, EnvironmentRendererRegistry, UiRendererRegistry, BackdropRendererRegistry,
        PostEnvironmentRendererRegistry, PostProcessingRendererRegistry, LifeCycleSystem {
    @Inject(optional = true)
    private DirectionalLightProvider directionalLightProvider;
    @Inject
    private RenderingEntityProvider renderingEntityProvider;
    @Inject
    private RenderingCameraProvider renderingCameraProvider;

    private PriorityCollection<BackdropRenderer> backdropRenderers = new PriorityCollection<>();
    private PriorityCollection<EnvironmentRenderer> environmentRenderers = new PriorityCollection<>();
    private PriorityCollection<PostEnvironmentRenderer> postEnvironmentRenderers = new PriorityCollection<>();
    private PriorityCollection<PostProcessingRenderer> postProcessingRenderers = new PriorityCollection<>();
    private PriorityCollection<UiRenderer> uiRenderers = new PriorityCollection<>();

    private PerspectiveCamera camera;
    private FrameBuffer lightFrameBuffer;
    private Camera lightCamera;

    private static int shadowFidelity = 4;

    private FrameBuffer firstOffScreenBuffer;
    private FrameBuffer secondOffScreenBuffer;

    private RenderingBuffer screenRenderingBuffer;
    private RenderingBuffer lightsRenderingBuffer;

    @Override
    public void registerBackdropRenderer(BackdropRenderer backdropRenderer) {
        backdropRenderers.add(backdropRenderer);
    }

    @Override
    public void registerEnvironmentRendered(EnvironmentRenderer environmentRenderer) {
        environmentRenderers.add(environmentRenderer);
    }

    @Override
    public void registerPostEnvironmentRenderer(PostEnvironmentRenderer postEnvironmentRenderer) {
        postEnvironmentRenderers.add(postEnvironmentRenderer);
    }

    @Override
    public void registerPostProcessingRenderer(PostProcessingRenderer postProcessingRenderer) {
        postProcessingRenderers.add(postProcessingRenderer);
    }

    @Override
    public void registerUiRenderer(UiRenderer uiRenderer) {
        uiRenderers.add(uiRenderer);
    }

    @Override
    public void preInitialize() {
        updateCamera();
        lightFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, shadowFidelity * 1024, shadowFidelity * 1024, true);
        lightCamera = new PerspectiveCamera(120f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        screenRenderingBuffer = new ScreenRenderingBuffer();
        lightsRenderingBuffer = new OffScreenRenderingBuffer(lightFrameBuffer);
    }

    @Override
    public void postDestroy() {
        lightFrameBuffer.dispose();
    }

    @Override
    public void updateCamera() {
        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (firstOffScreenBuffer != null) {
            firstOffScreenBuffer.dispose();
        }
        if (secondOffScreenBuffer != null) {
            secondOffScreenBuffer.dispose();
        }
        firstOffScreenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        secondOffScreenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void render() {
        //noinspection unchecked
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();

        if (renderingEntity != null) {
            Collection<PostProcessingRenderer> enabledPostProcessors = getEnabledPostProcessors(renderingEntity);

            setupCamera(renderingEntity);
            boolean hasDirectionalLight = setupDirectionalLight();
            if (hasDirectionalLight) {
                renderLightMap();
            }

            RenderingBuffer mainPassBuffer;
            if (enabledPostProcessors.isEmpty()) {
                mainPassBuffer = screenRenderingBuffer;
            } else {
                mainPassBuffer = new OffScreenRenderingBuffer(firstOffScreenBuffer);
            }

            float ambientLight = hasDirectionalLight ? directionalLightProvider.getAmbientLight() : 1f;
            renderCameraView(mainPassBuffer, hasDirectionalLight, ambientLight);

            if (!enabledPostProcessors.isEmpty()) {
                postProcess(renderingEntity, enabledPostProcessors);
            }
        } else {
            screenRenderingBuffer.begin();
            cleanBuffer();
            screenRenderingBuffer.end();
        }

        for (UiRenderer uiRenderer : uiRenderers) {
            uiRenderer.renderUi();
        }
    }

    private void postProcess(EntityRef observerEntity, Collection<PostProcessingRenderer> enabledPostProcessors) {
        FlipOffScreenRenderingBuffer buffer = new FlipOffScreenRenderingBuffer(firstOffScreenBuffer, secondOffScreenBuffer);
        Iterator<PostProcessingRenderer> iterator = enabledPostProcessors.iterator();
        while (iterator.hasNext()) {
            PostProcessingRenderer postProcessor = iterator.next();

            boolean hasNext = iterator.hasNext();
            RenderingBuffer resultBuffer = hasNext ? buffer : screenRenderingBuffer;

            buffer.flip();
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 2);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, buffer.getSourceBuffer().getColorBufferTexture().getTextureObjectHandle());
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 3);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, buffer.getSourceBuffer().getDepthBufferHandle());

            postProcessor.render(observerEntity, resultBuffer, camera, 2, 3);
        }
    }

    private void renderCameraView(RenderingBuffer mainPassBuffer, boolean hasDirectionalLight, float ambientLight) {
        mainPassBuffer.begin();
        cleanBuffer();
        renderBackdrop();
        normalRenderPass(hasDirectionalLight, ambientLight);
        renderPostEnvironment();
        mainPassBuffer.end();
    }

    private void renderLightMap() {
        lightsRenderingBuffer.begin();
        cleanBuffer();
        lightRenderPass();
        lightsRenderingBuffer.end();
    }

    private Collection<PostProcessingRenderer> getEnabledPostProcessors(EntityRef activeCameraEntity) {
        List<PostProcessingRenderer> renderers = new LinkedList<>();
        for (PostProcessingRenderer postProcessingRenderer : postProcessingRenderers) {
            if (postProcessingRenderer.isEnabled(activeCameraEntity))
                renderers.add(postProcessingRenderer);
        }

        return renderers;
    }

    private void cleanBuffer() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    private void renderBackdrop() {
        for (BackdropRenderer backdropRenderer : backdropRenderers) {
            backdropRenderer.renderBackdrop(camera);
        }
    }

    private void setupCamera(EntityRef renderingCameraEntity) {
        renderingCameraProvider.setupRenderingCamera(renderingCameraEntity, camera);
        camera.update();
    }

    private boolean setupDirectionalLight() {
        if (directionalLightProvider == null)
            return false;

        directionalLightProvider.setupLightCamera(lightCamera);
        lightCamera.update();
        return true;
    }

    private void lightRenderPass() {
        for (EnvironmentRenderer environmentRenderer : environmentRenderers) {
            environmentRenderer.renderEnvironmentForLight(lightCamera);
        }
    }

    private void normalRenderPass(boolean hasDirectionalLight, float ambientLight) {
        Texture lightTexture = lightFrameBuffer.getColorBufferTexture();
        for (EnvironmentRenderer environmentRenderer : environmentRenderers) {
            environmentRenderer.renderEnvironment(hasDirectionalLight, camera, lightCamera, lightTexture, shadowFidelity, ambientLight);
        }
    }

    private void renderPostEnvironment() {
        for (PostEnvironmentRenderer postEnvironmentRenderer : postEnvironmentRenderers) {
            postEnvironmentRenderer.renderPostEnvironment(camera);
        }
    }

    private class ScreenRenderingBuffer implements RenderingBuffer {
        @Override
        public void begin() {

        }

        @Override
        public void end() {

        }
    }

    private class OffScreenRenderingBuffer implements RenderingBuffer {
        private FrameBuffer frameBuffer;

        public OffScreenRenderingBuffer(FrameBuffer frameBuffer) {
            this.frameBuffer = frameBuffer;
        }

        @Override
        public void begin() {
            frameBuffer.begin();
        }

        @Override
        public void end() {
            frameBuffer.end();
        }
    }

    private class FlipOffScreenRenderingBuffer implements RenderingBuffer {
        private FrameBuffer firstFrameBuffer;
        private FrameBuffer secondFrameBuffer;
        private boolean drawsToFirst = true;

        public FlipOffScreenRenderingBuffer(FrameBuffer firstFrameBuffer, FrameBuffer secondFrameBuffer) {
            this.firstFrameBuffer = firstFrameBuffer;
            this.secondFrameBuffer = secondFrameBuffer;
        }

        @Override
        public void begin() {

        }

        @Override
        public void end() {

        }

        public void flip() {
            drawsToFirst = !drawsToFirst;
        }

        public FrameBuffer getSourceBuffer() {
            if (drawsToFirst)
                return secondFrameBuffer;
            else
                return firstFrameBuffer;
        }
    }
}
