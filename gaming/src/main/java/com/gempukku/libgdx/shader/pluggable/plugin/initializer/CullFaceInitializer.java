package com.gempukku.libgdx.shader.pluggable.plugin.initializer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.gempukku.libgdx.shader.BasicShader;
import com.gempukku.libgdx.shader.UniformRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableRenderInitializerCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class CullFaceInitializer implements PluggableRenderInitializerCall {
    private static int defaultCullFace = GL20.GL_BACK;

    private static PluggableShaderFeatureRegistry.PluggableShaderFeature cullFace = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(cullFace);
    }

    @Override
    public void appendInitializer(Renderable renderable, UniformRegistry uniformRegistry) {
        uniformRegistry.registerRenderInitializer(false,
                new UniformRegistry.RenderInitializer() {
                    @Override
                    public void initialize(BasicShader shader, Renderable renderable, Attributes combinedAttributes) {
                        int cullFace = defaultCullFace;
                        IntAttribute cullFaceAttr = combinedAttributes.get(IntAttribute.class, IntAttribute.CullFace);
                        if (cullFaceAttr != null)
                            cullFace = cullFaceAttr.value;

                        shader.getContext().setCullFace(cullFace);
                    }
                });
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return true;
    }
}
