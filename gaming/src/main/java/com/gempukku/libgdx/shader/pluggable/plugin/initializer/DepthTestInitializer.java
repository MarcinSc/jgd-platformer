package com.gempukku.libgdx.shader.pluggable.plugin.initializer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.gempukku.libgdx.shader.BasicShader;
import com.gempukku.libgdx.shader.UniformRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableRenderInitializerCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class DepthTestInitializer implements PluggableRenderInitializerCall {
    private static int defaultDepthFunc = GL20.GL_LEQUAL;

    private static PluggableShaderFeatureRegistry.PluggableShaderFeature depthTest = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(depthTest);
    }

    @Override
    public void appendInitializer(Renderable renderable, UniformRegistry uniformRegistry) {
        uniformRegistry.registerRenderInitializer(false,
                new UniformRegistry.RenderInitializer() {
                    @Override
                    public void initialize(BasicShader shader, Renderable renderable, Attributes combinedAttributes) {
                        int depthFunc = defaultDepthFunc;
                        float depthRangeNear = 0f;
                        float depthRangeFar = 1f;
                        boolean depthMask = true;

                        DepthTestAttribute dta = combinedAttributes.get(DepthTestAttribute.class, DepthTestAttribute.Type);
                        if (dta != null) {
                            depthFunc = dta.depthFunc;
                            depthRangeNear = dta.depthRangeNear;
                            depthRangeFar = dta.depthRangeFar;
                            depthMask = dta.depthMask;
                        }

                        shader.getContext().setDepthTest(depthFunc, depthRangeNear, depthRangeFar);
                        shader.getContext().setDepthMask(depthMask);
                    }
                });
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
