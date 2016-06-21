package com.gempukku.gaming.rendering.shape;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Map;

public class MapTextureRegionMapper implements TextureRegionMapper {
    private Map<String, TextureRegion> textureRegionMap;

    public MapTextureRegionMapper(Map<String, TextureRegion> textureRegionMap) {
        this.textureRegionMap = textureRegionMap;
    }

    @Override
    public TextureRegion getTextureRegion(String textureId) {
        return textureRegionMap.get(textureId);
    }
}
