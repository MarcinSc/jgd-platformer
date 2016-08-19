package com.gempukku.gaming.asset.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RegisterSystem(
        profiles = "textureAtlas", shared = {TextureAtlasProvider.class, TextureAtlasRegistry.class})
public class ReflectionsTextureAtlasProvider implements TextureAtlasProvider, TextureAtlasRegistry, LifeCycleSystem {

    private Map<String, TextureAtlas> textureAtlases;
    private Map<String, List<Texture>> textureList;
    private Map<String, Map<String, TextureRegion>> textures;

    private Multimap<String, String> texturesToRegister = HashMultimap.create();

    @Override
    public void registerTextures(String textureAtlasId, Collection<String> textures) {
        texturesToRegister.putAll(textureAtlasId, textures);
    }

    @Override
    public void postInitialize() {
        textureList = new HashMap<>();
        textures = new HashMap<>();
        textureAtlases = new HashMap<>();

        for (String textureAtlasId : texturesToRegister.keySet()) {
            PixmapPacker packer = new PixmapPacker(512, 512, Pixmap.Format.RGBA8888, 2, false);
            packer.setDuplicateBorder(true);

            for (String texturePath : texturesToRegister.get(textureAtlasId)) {
                packer.pack(texturePath, new Pixmap(Gdx.files.internal(texturePath)));
            }

            TextureAtlas textureAtlas = packer.generateTextureAtlas(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false);

            Map<String, TextureRegion> textureMapInAtlas = new HashMap<>();
            for (TextureAtlas.AtlasRegion atlasRegion : textureAtlas.getRegions()) {
                String name = atlasRegion.name;
                textureMapInAtlas.put(name, atlasRegion);
            }

            List<Texture> texturesInAtlas = new ArrayList<>();
            Iterables.addAll(texturesInAtlas, textureAtlas.getTextures());

            textureList.put(textureAtlasId, texturesInAtlas);
            textures.put(textureAtlasId, textureMapInAtlas);
            textureAtlases.put(textureAtlasId, textureAtlas);
        }

        texturesToRegister = null;
    }

    @Override
    public List<Texture> getTextures(String textureAtlasId) {
        return Collections.unmodifiableList(textureList.get(textureAtlasId));
    }

    @Override
    public TextureRegion getTexture(String textureAtlasId, String name) {
        return textures.get(textureAtlasId).get(name);
    }
}
