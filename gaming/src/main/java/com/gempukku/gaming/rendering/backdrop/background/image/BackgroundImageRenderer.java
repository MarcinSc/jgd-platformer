package com.gempukku.gaming.rendering.backdrop.background.image;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.rendering.event.RenderBackdrop;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "backgroundImage"
)
public class BackgroundImageRenderer implements LifeCycleSystem {
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private ModelBatch modelBatch;

    private BackgroundImageShaderProvider backgroundImageProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void preInitialize() {
        backgroundImageProvider = new BackgroundImageShaderProvider();

        modelBatch = new ModelBatch(backgroundImageProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("background", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position,
                new Material(new DepthTestAttribute(false)));
        backgroundBuilder.rect(
                -1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                1, 1, 1,
                0, 0, 1);
        model = modelBuilder.end();

        modelInstance = new ModelInstance(model);
    }

    @ReceiveEvent(priorityName = "gaming.renderer.backgroundImage")
    public void renderBackground(RenderBackdrop event, EntityRef renderingEntity, BackgroundImageComponent backgroundImage) {
        TextureRegion texture = textureAtlasProvider.getTexture(backgroundImage.getTextureAtlasId(), backgroundImage.getTextureName());

        float paddingLeft = backgroundImage.getPaddingLeft();
        float paddingBottom = backgroundImage.getPaddingBottom();
        float paddingRight = backgroundImage.getPaddingRight();
        float paddingTop = backgroundImage.getPaddingTop();

        float viewportWidth = event.getCamera().viewportWidth;
        float viewportHeight = event.getCamera().viewportHeight;

        int textureWidth = texture.getRegionWidth();
        int textureHeight = texture.getRegionHeight();

        float widthPaddingMultiplier = 1 + paddingLeft + paddingRight;
        float heightPaddingMultiplier = 1 + paddingTop + paddingBottom;

        float regionWidth = textureWidth * widthPaddingMultiplier;
        float regionHeight = textureHeight * heightPaddingMultiplier;

        float viewportRatio = viewportWidth / viewportHeight;
        float regionRatio = regionWidth / regionHeight;

        float leftEdge;
        float topEdge;
        float rightEdge;
        float bottomEdge;

        float width;
        float height;
        if (viewportRatio > regionRatio) {
            width = 1f;
            height = regionRatio / viewportRatio;
        } else {
            width = viewportRatio / regionRatio;
            height = 1f;
        }

        // Discounting padding for a moment
        leftEdge = 0.5f - width / 2;
        rightEdge = 0.5f + width / 2;
        topEdge = 0.5f - height / 2;
        bottomEdge = 0.5f + height / 2;

        // Apply bounds for padding
        leftEdge -= paddingLeft * width;
        rightEdge += paddingRight * width;
        bottomEdge += paddingBottom * height;
        topEdge -= paddingTop * height;

        backgroundImageProvider.setBackgroundImageStartX(texture.getU());
        backgroundImageProvider.setBackgroundImageStartY(texture.getV());
        backgroundImageProvider.setBackgroundImageWidth(texture.getU2() - texture.getU());
        backgroundImageProvider.setBackgroundImageHeight(texture.getV2() - texture.getV());

        backgroundImageProvider.setLeftEdge(leftEdge);
        backgroundImageProvider.setTopEdge(topEdge);
        backgroundImageProvider.setRightEdge(rightEdge);
        backgroundImageProvider.setBottomEdge(bottomEdge);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, texture.getTexture().getTextureObjectHandle());

        backgroundImageProvider.setBackgroundImageIndex(0);
        backgroundImageProvider.setBackgroundColor(new Vector3(
                backgroundImage.getBackgroundRed() / 255f,
                backgroundImage.getBackgroundGreen() / 255f,
                backgroundImage.getBackgroundBlue() / 255f));

        event.getRenderPipeline().getCurrentBuffer().begin();
        modelBatch.begin(event.getCamera());
        modelBatch.render(modelInstance);
        modelBatch.end();
        event.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }
}
