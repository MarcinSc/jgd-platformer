package com.gempukku.libgdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.GLOnlyTextureData;
import com.badlogic.gdx.utils.Disposable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OpenGLPool {
    private List<DefaultRenderBuffer> oldRenderBuffers = new LinkedList<>();
    private List<DefaultRenderBuffer> newRenderBuffers = new LinkedList<>();

    private List<Texture> oldColorTextures = new LinkedList<>();
    private List<Texture> newColorTextures = new LinkedList<>();

    private List<Texture> oldDepthStencilTextures = new LinkedList<>();
    private List<Texture> newDepthStencilTextures = new LinkedList<>();

    public void ageOutDisposables() {
        ageOutDisposables(oldColorTextures, newColorTextures);
        ageOutDisposables(oldDepthStencilTextures, newDepthStencilTextures);
        ageOutDisposables(oldRenderBuffers, newRenderBuffers);
    }

    private <T extends Disposable> void ageOutDisposables(List<T> oldDisposables, List<T> newDisposables) {
        for (T texture : oldDisposables) {
            texture.dispose();
        }
        oldDisposables.clear();
        oldDisposables.addAll(newDisposables);
        newDisposables.clear();
    }

    public void cleanup() {
        cleanup(oldColorTextures, newColorTextures);
        cleanup(oldDepthStencilTextures, newDepthStencilTextures);
        cleanup(oldRenderBuffers, newRenderBuffers);
    }

    private <T extends Disposable> void cleanup(List<T> oldDisposables, List<T> newDisposables) {
        for (T texture : oldDisposables) {
            texture.dispose();
        }
        for (T texture : newDisposables) {
            texture.dispose();
        }
        oldDisposables.clear();
        newDisposables.clear();
    }

    public Texture getNewColorTexture(int width, int height) {
        Texture texture = extractTexture(width, height, this.newColorTextures);
        if (texture != null) return texture;
        texture = extractTexture(width, height, this.oldColorTextures);
        if (texture != null) return texture;

        GLOnlyTextureData data = new GLOnlyTextureData(width, height, 0, GL20.GL_RGBA, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE);
        Texture result = new Texture(data);
        result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        return result;
    }

    public Texture getNewDepthStencilTexture(int width, int height) {
        Texture texture = extractTexture(width, height, this.newDepthStencilTextures);
        if (texture != null) return texture;
        texture = extractTexture(width, height, this.oldDepthStencilTextures);
        if (texture != null) return texture;

        GLOnlyTextureData data = new GLOnlyTextureData(width, height, 0, GL30.GL_DEPTH24_STENCIL8, GL30.GL_DEPTH_STENCIL, GL30.GL_UNSIGNED_INT_24_8);
        Texture result = new Texture(data);
        result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        return result;
    }

    public RenderBuffer getNewRenderBuffer(int width, int height) {
        Iterator<DefaultRenderBuffer> newIterator = newRenderBuffers.iterator();
        if (newIterator.hasNext()) {
            DefaultRenderBuffer result = newIterator.next();
            newIterator.remove();

            result.resize(width, height);

            return result;
        }

        return new DefaultRenderBuffer(width, height);
    }

    public void returnColorTexture(Texture texture) {
        newColorTextures.add(texture);
    }

    public void returnDepthStencilTexture(Texture texture) {
        newDepthStencilTextures.add(texture);
    }

    public void returnRenderBuffer(RenderBuffer renderBuffer) {
        DefaultRenderBuffer defaultRenderBuffer = (DefaultRenderBuffer) renderBuffer;
        defaultRenderBuffer.clear();
        newRenderBuffers.add(defaultRenderBuffer);
    }

    private Texture extractTexture(int width, int height, List<Texture> textures) {
        Iterator<Texture> iterator = textures.iterator();
        while (iterator.hasNext()) {
            Texture texture = iterator.next();
            if (texture.getWidth() == width && texture.getHeight() == height) {
                iterator.remove();
                return texture;
            }
        }
        return null;
    }
}
