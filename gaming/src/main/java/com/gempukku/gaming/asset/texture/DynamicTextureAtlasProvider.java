package com.gempukku.gaming.asset.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RegisterSystem(
        profiles = "textureAtlas", shared = TextureAtlasProvider.class)
public class DynamicTextureAtlasProvider implements TextureAtlasProvider {
    private Map<String, PixmapPacker> packers = new HashMap<>();
    private Map<String, TextureAtlas> textureAtlases = new HashMap<>();
    private Map<String, Map<String, TextureRegion>> textures = new HashMap<>();

    @Override
    public List<Texture> getTextures(String textureAtlasId) {
        TextureAtlas textureAtlas = textureAtlases.get(textureAtlasId);
        if (textureAtlas == null)
            return Collections.emptyList();
        List<Texture> texturesInAtlas = new ArrayList<>();
        Iterables.addAll(texturesInAtlas, textureAtlas.getTextures());
        return texturesInAtlas;
    }

    @Override
    public TextureRegion getTexture(String textureAtlasId, String name) {
        Map<String, TextureRegion> textureRegionMap = textures.get(textureAtlasId);
        if (textureRegionMap == null) {
            PixmapPacker packer = new PixmapPacker(512, 512, Pixmap.Format.RGBA8888, 2, false);
            packer.setDuplicateBorder(true);
            packers.put(textureAtlasId, packer);
            TextureAtlas textureAtlas = new TextureAtlas();
            textureAtlases.put(textureAtlasId, textureAtlas);
            textureRegionMap = new HashMap<>();
            textures.put(textureAtlasId, textureRegionMap);
        }

        TextureRegion textureRegion = textureRegionMap.get(name);
        if (textureRegion == null) {
            TextureAtlas textureAtlas = textureAtlases.get(textureAtlasId);
            PixmapPacker packer = packers.get(textureAtlasId);
            packer.pack(name, new Pixmap(Gdx.files.internal(name)));
            packer.updateTextureAtlas(textureAtlas, Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false);
            textureRegion = textureAtlas.findRegion(name);
            textureRegionMap.put(name, textureRegion);
        }
        return textureRegion;
    }
}
