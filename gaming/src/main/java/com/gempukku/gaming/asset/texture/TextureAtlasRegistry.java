package com.gempukku.gaming.asset.texture;

import java.util.Collection;

public interface TextureAtlasRegistry {
    void registerTextures(String textureAtlasId, Collection<String> textures);
}
