package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PluggableShader extends BaseShader {
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

    public PluggableShader(ShaderProgram shaderProgram, Renderable renderable) {
        this.shaderProgram = shaderProgram;
        this.renderable = renderable;
    }

    @Override
    public void init() {
        super.init(shaderProgram, renderable);
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
//            if (BlendingAttribute.is(t)) {
//                context.setBlending(true, ((BlendingAttribute)attr).sourceFunction, ((BlendingAttribute)attr).destFunction);
//                set(u_opacity, ((BlendingAttribute)attr).opacity);
//            } else if ((t & IntAttribute.CullFace) == IntAttribute.CullFace)
            if ((t & IntAttribute.CullFace) == IntAttribute.CullFace)
                cullFace = ((IntAttribute) attr).value;
//            else if ((t & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest)
//                set(u_alphaTest, ((FloatAttribute)attr).value);
            else if ((t & DepthTestAttribute.Type) == DepthTestAttribute.Type) {
                DepthTestAttribute dta = (DepthTestAttribute) attr;
                depthFunc = dta.depthFunc;
                depthRangeNear = dta.depthRangeNear;
                depthRangeFar = dta.depthRangeFar;
                depthMask = dta.depthMask;
//            } else if (!config.ignoreUnimplemented)
//                throw new GdxRuntimeException("Unknown material attribute: " + attr.toString());
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
