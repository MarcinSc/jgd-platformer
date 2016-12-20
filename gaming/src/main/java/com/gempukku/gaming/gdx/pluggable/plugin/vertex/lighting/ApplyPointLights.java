package com.gempukku.gaming.gdx.pluggable.plugin.vertex.lighting;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.utils.Array;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderFeatures;
import com.gempukku.gaming.gdx.pluggable.UniformRegistry;
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class ApplyPointLights implements PerVertexLightingCalculateFunctionCall {
    private int numPointLights;
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature variableShininess = PluggableShaderFeatureRegistry.registerFeature();
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature constantShininess = PluggableShaderFeatureRegistry.registerFeature();
    // Separate due to config
    private PluggableShaderFeatureRegistry.PluggableShaderFeature applyPointLights = PluggableShaderFeatureRegistry.registerFeature();

    public ApplyPointLights(int numPointLights) {
        this.numPointLights = numPointLights;
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applyPointLights";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(applyPointLights);
        boolean hasShininess = renderable.material.has(FloatAttribute.Shininess);
        if (hasShininess)
            pluggableShaderFeatures.addFeature(variableShininess);
        else
            pluggableShaderFeatures.addFeature(constantShininess);
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder, boolean hasSpecular) {
        vertexShaderBuilder.addStructure("PointLight",
                "  vec3 color;\n" +
                        "  vec3 position;\n");
        vertexShaderBuilder.addStructArrayUniformVariable("u_pointLights", new String[]{"color", "position"}, numPointLights, "PointLight",
                new UniformRegistry.Setter() {
                    @Override
                    public boolean isGlobal(BaseShader shader) {
                        return false;
                    }

                    @Override
                    public void set(BaseShader shader, int startingLocation, int[] fieldOffsets, int structSize, Renderable renderable, Attributes combinedAttributes) {
                        final PointLightsAttribute pla = combinedAttributes.get(PointLightsAttribute.class, PointLightsAttribute.Type);
                        final Array<PointLight> points = pla == null ? null : pla.lights;

                        for (int i = 0; i < numPointLights; i++) {
                            int idx = startingLocation + i * structSize;
                            if (points != null && i < points.size) {
                                PointLight pointLight = points.get(i);

                                shader.program.setUniformf(idx, pointLight.color.r * pointLight.intensity,
                                        pointLight.color.g * pointLight.intensity, pointLight.color.b * pointLight.intensity);
                                shader.program.setUniformf(idx + fieldOffsets[1], pointLight.position.x, pointLight.position.y,
                                        pointLight.position.z);
                                // Check if there is no second argument
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
        function.append("Lighting applyPointLights(vec4 pos, Lighting lighting) {\n");
        if (hasSpecular)
            function.append("  vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);\n");
        function.append("  for (int i = 0; i < " + numPointLights + "; i++) {\n" +
                "    vec3 lightDir = u_pointLights[i].position - pos.xyz;\n" +
                "    float dist2 = dot(lightDir, lightDir);\n" +
                "    lightDir *= inversesqrt(dist2);\n" +
                "    float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);\n" +
                "    vec3 value = u_pointLights[i].color * (NdotL / (1.0 + dist2));\n" +
                "    lighting.diffuse += value;\n");
        if (hasSpecular) {
            function.append("    float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));\n" +
                    "    lighting.specular += value * pow(halfDotView, u_shininess);\n");
        }
        function.append("  }\n");
        function.append("  return lighting;\n");
        function.append("}\n");

        vertexShaderBuilder.addFunction("applyPointLights", function.toString());
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return numPointLights > 0 && (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) > 0;
    }
}
