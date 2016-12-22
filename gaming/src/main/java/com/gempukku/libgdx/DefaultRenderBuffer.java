package com.gempukku.libgdx;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class DefaultRenderBuffer implements RenderBuffer, Disposable {
    private static boolean DEBUG = false;

    /**
     * the default framebuffer handle, a.k.a screen.
     */
    private static int defaultFramebufferHandle;
    private static int currentlyBoundFrameBufferHandle;
    /**
     * true if we have polled for the default handle already.
     */
    private static boolean defaultFramebufferHandleInitialized = false;

    /**
     * the framebuffer handle
     **/
    private int framebufferHandle;
    private int width;
    private int height;

    private Texture colorBuffer;
    private Texture depthStencilBuffer;

    public DefaultRenderBuffer(int width, int height) {
        this.width = width;
        this.height = height;

        init();
    }

    private void init() {
        GL20 gl = Gdx.gl20;

        // iOS uses a different framebuffer handle! (not necessarily 0)
        if (!defaultFramebufferHandleInitialized) {
            defaultFramebufferHandleInitialized = true;
            if (Gdx.app.getType() == Application.ApplicationType.iOS) {
                IntBuffer intbuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder()).asIntBuffer();
                gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, intbuf);
                defaultFramebufferHandle = intbuf.get(0);
            } else {
                defaultFramebufferHandle = 0;
            }
            currentlyBoundFrameBufferHandle = defaultFramebufferHandle;
        }

        framebufferHandle = gl.glGenFramebuffer();
    }

    public void clear() {
        begin();
        if (colorBuffer != null)
            setColorBuffer(null);
        if (depthStencilBuffer != null)
            setDepthStencilBuffer(null);
        end();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void dispose() {
        GL20 gl = Gdx.gl20;

        gl.glDeleteFramebuffer(framebufferHandle);
    }

    @Override
    public void begin() {
        bind(framebufferHandle, width, height);
    }

    @Override
    public void end() {
        bind(defaultFramebufferHandle, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    private static void bind(int frameBufferHandle, int width, int height) {
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, frameBufferHandle);
        Gdx.gl20.glViewport(0, 0, width, height);
        currentlyBoundFrameBufferHandle = frameBufferHandle;
    }

    @Override
    public Texture getColorBuffer() {
        return colorBuffer;
    }

    @Override
    public Texture getDepthStencilBuffer() {
        return depthStencilBuffer;
    }

    @Override
    public void setColorBuffer(Texture texture) {
        validateFrameBufferHandle();
        if (texture != null && (texture.getWidth() != width || texture.getHeight() != height))
            throw new IllegalArgumentException("Invalid buffer size");

        this.colorBuffer = texture;

        if (texture != null) {
            Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, texture.getTextureObjectHandle());

            Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D,
                    texture.getTextureObjectHandle(), 0);

            if (DEBUG) {
                int result = Gdx.gl20.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);
                System.out.println(result);
            }
        } else {
            Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D, 0, 0);
        }
    }

    @Override
    public void setDepthStencilBuffer(Texture texture) {
        validateFrameBufferHandle();
        if (texture != null && (texture.getWidth() != width || texture.getHeight() != height))
            throw new IllegalArgumentException("Invalid buffer size");

        this.depthStencilBuffer = texture;

        if (texture != null) {
            Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, texture.getTextureObjectHandle());

            Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_TEXTURE_2D,
                    texture.getTextureObjectHandle(), 0);
            Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT, GL20.GL_TEXTURE_2D,
                    texture.getTextureObjectHandle(), 0);

            if (DEBUG) {
                int result = Gdx.gl20.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);
                System.out.println(result);
            }
        } else {
            Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_TEXTURE_2D, 0, 0);
            Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT, GL20.GL_TEXTURE_2D, 0, 0);
        }
    }

    private void validateFrameBufferHandle() {
        if (currentlyBoundFrameBufferHandle != framebufferHandle)
            throw new IllegalStateException("This RenderBuffer is not currently in use");
    }
}
