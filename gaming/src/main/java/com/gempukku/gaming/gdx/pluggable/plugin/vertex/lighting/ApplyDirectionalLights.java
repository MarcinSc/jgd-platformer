package com.gempukku.gaming.gdx.pluggable.plugin.vertex.lighting;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.utils.Array;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.UniformRegistry;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class ApplyDirectionalLights implements PerVertexLightingCalculateFunctionCall {
    private int numDirectionalLights;
    // Separate due to config
    private PluggableShaderFeatureRegistry.PluggableShaderFeature applyDirectionalLights = PluggableShaderFeatureRegistry.registerFeature();

    public ApplyDirectionalLights(int numDirectionalLights) {
        this.numDirectionalLights = numDirectionalLights;
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applyDirectionalLights";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(applyDirectionalLights);
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder, boolean hasSpecular) {
        vertexShaderBuilder.addStructure("DirectionalLight",
                "  vec3 color;\n" +
                        "  vec3 direction;\n");
        vertexShaderBuilder.addStructArrayUniformVariable("u_dirLights", new String[]{"color", "direction"}, numDirectionalLights, "DirectionalLight",
                new UniformRegistry.Setter() {
                    @Override
                    public boolean isGlobal(BaseShader shader) {
                        return false;
                    }

                    @Override
                    public void set(BaseShader shader, int startingLocation, int[] fieldOffsets, int structSize, Renderable renderable, Attributes combinedAttributes) {
                        final DirectionalLightsAttribute dla = combinedAttributes.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
                        final Array<DirectionalLight> dirs = dla == null ? null : dla.lights;

                        for (int i = 0; i < numDirectionalLights; i++) {
                            int idx = startingLocation + i * structSize;
                            if (dirs != null && i < dirs.size) {
                                DirectionalLight directionalLight = dirs.get(i);

                                shader.program.setUniformf(idx, directionalLight.color.r, directionalLight.color.g,
                                        directionalLight.color.b);
                                shader.program.setUniformf(idx + fieldOffsets[1], directionalLight.direction.x,
                                        directionalLight.direction.y, directionalLight.direction.z);
                                // Check if there is no second element
                                if (structSize <= 0) break;
                            } else {
                                shader.program.setUniformf(idx, 0, 0, 0);
                                shader.program.setUniformf(idx + fieldOffsets[1], 0, 0, 0);
                            }
                        }
                    }
                });

        if (hasSpecular) {
            vertexShaderBuilder.addUniformVariable("u_cameraPosition", "vec4", DefaultShader.Setters.cameraPosition);
            boolean hasShininess = renderable.material.has(FloatAttribute.Shininess);
            if (hasShininess)
                vertexShaderBuilder.addUniformVariable("u_shininess", "float", DefaultShader.Setters.shininess);
            else
                vertexShaderBuilder.addUniformVariable("u_shininess", "float", LightingUtils.defaultShininessSetter);
        }

        StringBuilder function = new StringBuilder();
        function.append("Lighting applyDirectionalLights(vec4 pos, Lighting lighting) {\n");
        if (hasSpecular)
            function.append("  vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);\n");
        function.append("  for (int i = 0; i < " + numDirectionalLights + "; i++) {\n" +
                "    vec3 lightDir = -u_dirLights[i].direction;\n" +
                "    float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);\n" +
                "    vec3 value = u_dirLights[i].color * NdotL;\n" +
                "    lighting.diffuse += value;\n");
        if (hasSpecular) {
            function.append("    float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));\n" +
                    "    lighting.specular += value * pow(halfDotView, u_shininess);\n");
        }
        function.append("  }\n");
        function.append("  return lighting;\n");
        function.append("}\n");

        vertexShaderBuilder.addFunction("applyDirectionalLights", function.toString());
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return numDirectionalLights > 0 && (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) > 0;
    }
}
