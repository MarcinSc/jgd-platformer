package com.gempukku.gaming.rendering.shape;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FixedTextureRegionMapper implements TextureRegionMapper {
    private TextureRegion textureRegion;

    public FixedTextureRegionMapper(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public TextureRegion getTextureRegion(String textureId) {
        return textureRegion;
    }
}
