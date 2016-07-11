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
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.rendering.event.RenderBackdrop;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem
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

    @ReceiveEvent
    public void renderBackground(RenderBackdrop event, EntityRef renderingEntity, BackgroundImageComponent backgroundImage) {
        TextureRegion texture = textureAtlasProvider.getTexture(backgroundImage.getTextureAtlasId(), backgroundImage.getTextureName());

        float viewportWidth = event.getCamera().viewportWidth;
        float viewportHeight = event.getCamera().viewportHeight;

        int regionWidth = texture.getRegionWidth();
        int regionHeight = texture.getRegionHeight();

        float viewportRatio = viewportWidth / viewportHeight;
        float regionRatio = regionWidth * 1f / regionHeight;

        float startX;
        float startY;
        float width;
        float height;

        if (viewportRatio > regionRatio) {
            startX = texture.getU();
            width = texture.getU2() - texture.getU();
            height = (texture.getV2() - texture.getV()) / viewportRatio;
            startY = texture.getV() + ((texture.getV2() - texture.getV()) - height) / 2f;
        } else {
            startY = texture.getV();
            height = texture.getV2() - texture.getV();
            width = (texture.getU2() - texture.getU()) * viewportRatio;
            startX = texture.getU() + ((texture.getU2() - texture.getU()) - width) / 2f;
        }

        backgroundImageProvider.setBackgroundImageStartX(startX);
        backgroundImageProvider.setBackgroundImageStartY(startY);
        backgroundImageProvider.setBackgroundImageWidth(width);
        backgroundImageProvider.setBackgroundImageHeight(height);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, texture.getTexture().getTextureObjectHandle());

        backgroundImageProvider.setBackgroundImageIndex(0);

        modelBatch.begin(event.getCamera());
        modelBatch.render(modelInstance);
        modelBatch.end();
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }
}
