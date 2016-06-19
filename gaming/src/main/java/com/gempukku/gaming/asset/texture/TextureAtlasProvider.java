package com.gempukku.gaming.asset.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;

public interface TextureAtlasProvider {
    List<Texture> getTextures(String textureAtlasId);

    TextureRegion getTexture(String textureAtlasId, String name);
}
