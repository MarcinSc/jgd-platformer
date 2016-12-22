package com.gempukku.libgdx.shader.pluggable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.gempukku.libgdx.shader.BasicShader;

public class PluggableShader extends BasicShader {
    /**
     * @deprecated Replaced by {@link DefaultShader.Config#defaultCullFace} Set to 0 to disable culling
     */
    @Deprecated
    public static int defaultCullFace = GL20.GL_BACK;
    /**
     * @deprecated Replaced by {@link DefaultShader.Config#defaultDepthFunc} Set to 0 to disable depth test
     */
    @Deprecated
    public static int defaultDepthFunc = GL20.GL_LEQUAL;

    private ShaderProgram shaderProgram;
    private Renderable renderable;

    public PluggableShader(Renderable renderable) {
        this.renderable = renderable;
    }

    public void setProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    @Override
    public int compareTo(Shader other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canRender(Renderable instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init() {
        init(shaderProgram, renderable);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
    }

    @Override
    public void render(Renderable renderable, Attributes combinedAttributes) {
        bindMaterial(combinedAttributes);
        super.render(renderable, combinedAttributes);
    }

    private void bindMaterial(final Attributes attributes) {
        int cullFace = defaultCullFace;
        int depthFunc = defaultDepthFunc;
        float depthRangeNear = 0f;
        float depthRangeFar = 1f;
        boolean depthMask = true;

        for (final Attribute attr : attributes) {
            final long t = attr.type;
            if ((t & IntAttribute.CullFace) == IntAttribute.CullFace)
                cullFace = ((IntAttribute) attr).value;
            else if ((t & DepthTestAttribute.Type) == DepthTestAttribute.Type) {
                DepthTestAttribute dta = (DepthTestAttribute) attr;
                depthFunc = dta.depthFunc;
                depthRangeNear = dta.depthRangeNear;
                depthRangeFar = dta.depthRangeFar;
                depthMask = dta.depthMask;
            }
        }

        context.setCullFace(cullFace);
        context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar);
        context.setDepthMask(depthMask);
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
        super.dispose();
    }
}
