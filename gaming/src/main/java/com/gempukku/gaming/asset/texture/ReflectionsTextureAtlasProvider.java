package com.gempukku.gaming.asset.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.gempukku.gaming.asset.component.NameComponentManager;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.io.File;
import java.net.URL;
import java.util.*;

@RegisterSystem(
        profiles = "textureAtlas", shared = {TextureAtlasProvider.class, TextureAtlasRegistry.class})
public class ReflectionsTextureAtlasProvider implements TextureAtlasProvider, TextureAtlasRegistry, LifeCycleSystem {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private NameComponentManager terasologyComponentManager;

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
        // Delete everything from the temp directory
        FileHandle temp = Gdx.files.local("temp");
        if (temp.exists()) {
            File atlasLocation = temp.file();

            for (File file : atlasLocation.listFiles()) {
                file.delete();
            }
        }

        textureList = new HashMap<>();
        textures = new HashMap<>();
        textureAtlases = new HashMap<>();

        for (String textureAtlasId : texturesToRegister.keySet()) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.maxWidth = 512;
            settings.maxHeight = 512;
            settings.silent = true;
            settings.duplicatePadding = true;

            File resourceRoot = new File(ReflectionsTextureAtlasProvider.class.getResource("/badlogic.jpg").getPath()).getParentFile().getParentFile();
            TexturePacker texturePacker = new TexturePacker(resourceRoot, settings);

            for (String texturePath : texturesToRegister.get(textureAtlasId)) {
                URL textureResource = ReflectionsTextureAtlasProvider.class.getResource("/" + texturePath);
                texturePacker.addImage(new File(textureResource.getPath()));
            }

            FileHandle atlasFileHandle = Gdx.files.local("temp/" + textureAtlasId + ".atlas");

            texturePacker.pack(temp.file(), textureAtlasId);

            TextureAtlas textureAtlas = new TextureAtlas(atlasFileHandle);

            Map<String, TextureRegion> textureMapInAtlas = new HashMap<>();
            for (TextureAtlas.AtlasRegion atlasRegion : textureAtlas.getRegions()) {
                String name = atlasRegion.name;
                textureMapInAtlas.put(name.substring(name.indexOf('/') + 1), atlasRegion);
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
        return textures.get(textureAtlasId).get(name.substring(0, name.lastIndexOf('.')));
    }
}
