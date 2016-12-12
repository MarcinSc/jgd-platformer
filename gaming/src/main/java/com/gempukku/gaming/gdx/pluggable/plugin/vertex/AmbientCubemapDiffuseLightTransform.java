package com.gempukku.gaming.gdx.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.PluggableVertexFunctionCall;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class AmbientCubemapDiffuseLightTransform implements PluggableVertexFunctionCall {
    // This one depends on the configuration, so separate feature instance per object
    private PluggableShaderFeatureRegistry.PluggableShaderFeature ambientCubemapDiffuseLightTransform = PluggableShaderFeatureRegistry.registerFeature();

    private int numDirectionalLights;
    private int numPointLights;

    public AmbientCubemapDiffuseLightTransform(int numDirectionalLights, int numPointLights) {
        this.numDirectionalLights = numDirectionalLights;
        this.numPointLights = numPointLights;
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "transformDiffuseLightWithAmbientCubemap";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(ambientCubemapDiffuseLightTransform);
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addUniformVariable("u_ambientCubemap[6]", "vec3", new DefaultShader.Setters.ACubemap(numDirectionalLights, numPointLights));
        vertexShaderBuilder.addFunction("transformDiffuseLightWithAmbientCubemap",
                "vec3 transformDiffuseLightWithAmbientCubemap(vec3 diffuseLight) {\n" +
                        "  vec3 squaredNormal = normal * normal;\n" +
                        "  vec3 isPositive  = step(0.0, normal);\n" +
                        "  diffuseLight += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +\n" +
                        "    squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +\n" +
                        "    squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);\n" +
                        "  return diffuseLight;\n" +
                        "}\n");
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return renderable.environment != null;
    }
}
