package com.gempukku.gaming.rendering.postprocess.pip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.gaming.rendering.CopyShaderProvider;
import com.gempukku.gaming.rendering.RenderingEngine;
import com.gempukku.gaming.rendering.event.PostProcessRendering;
import com.gempukku.gaming.rendering.postprocess.RenderPipeline;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;

@RegisterSystem(
        profiles = "pictureInPicture")
public class PictureInPicturePostProcessor implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private RenderingEngine renderingEngine;

    private EntityIndex pictureInPictureSources;
    private CopyShaderProvider copyShaderProvider;
    private ModelBatch copyModelBatch;
    private ShapeRenderer shapeRenderer;

    @Override
    public void initialize() {
        pictureInPictureSources = entityIndexManager.addIndexOnComponents(PictureInPictureSourceComponent.class);

        copyShaderProvider = new CopyShaderProvider();
        copyModelBatch = new ModelBatch(copyShaderProvider);
        shapeRenderer = new ShapeRenderer();
    }

    @ReceiveEvent
    public void postProcess(PostProcessRendering event, EntityRef entityRef, PictureInPictureRenderingComponent pictureInPictureRendering) {
        RenderPipeline renderPipeline = event.getRenderPipeline();
        FrameBuffer currentBuffer = renderPipeline.getCurrentBuffer();

        Camera camera = event.getCamera();
        float screenWidth = camera.viewportWidth;
        float screenHeight = camera.viewportHeight;

        for (EntityRef pictureInPictureSource : pictureInPictureSources) {
            PictureInPictureSourceComponent pipSource = pictureInPictureSource.getComponent(PictureInPictureSourceComponent.class);
            Vector2 location = pipSource.getLocation();
            Vector2 size = pipSource.getSize();
            Color frameColor = pipSource.getFrameColor();

            int width = MathUtils.round(camera.viewportWidth * size.x);
            int height = MathUtils.round(camera.viewportHeight * size.y);

            FrameBuffer pipBuffer = renderPipeline.getNewFrameBuffer(width, height, true);
            renderPipeline.setCurrentBuffer(pipBuffer);

            pipBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            pipBuffer.end();

            renderingEngine.renderEntity(renderPipeline, pictureInPictureSource, width, height);

            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            MeshPartBuilder backgroundBuilder = modelBuilder.part("screen", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position,
                    new Material(new DepthTestAttribute(0, false)));
            backgroundBuilder.rect(
                    location.x, location.y + size.y, 1,
                    location.x, location.y, 1,
                    location.x + size.x, location.y, 1,
                    location.x + size.x, location.y + size.y, 1,
                    0, 0, 1);
            Model copyModel = modelBuilder.end();

            ModelInstance copyModelInstance = new ModelInstance(copyModel);

            copyShaderProvider.setSourceTextureIndex(0);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            FrameBuffer currentPipBuffer = renderPipeline.getCurrentBuffer();
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, currentPipBuffer.getColorBufferTexture().getTextureObjectHandle());

            copyShaderProvider.setTextureStart(location.x, location.y);
            copyShaderProvider.setTextureSize(size.x, size.y);

            currentBuffer.begin();
            copyModelBatch.begin(null);
            copyModelBatch.render(copyModelInstance);
            copyModelBatch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(frameColor);
            shapeRenderer.rect(
                    screenWidth * location.x, screenHeight * location.y,
                    screenWidth * size.x, screenHeight * size.y);
            shapeRenderer.end();

            currentBuffer.end();

            renderPipeline.returnFrameBuffer(currentPipBuffer);
            copyModel.dispose();
        }
        renderPipeline.setCurrentBuffer(currentBuffer);
    }
}
