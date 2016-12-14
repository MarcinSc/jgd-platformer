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
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class ApplyDirectionalLights implements PerVertexLightingFunctionCall {
    private int numDirectionalLights;
    // Separate due to config
    private PluggableShaderFeatureRegistry.PluggableShaderFeature applyDirectionalLights = PluggableShaderFeatureRegistry.registerFeature();

    private DirectionalLight[] directionalLights;

    public ApplyDirectionalLights(int numDirectionalLights) {
        this.numDirectionalLights = numDirectionalLights;
        this.directionalLights = new DirectionalLight[numDirectionalLights];
        for (int i = 0; i < directionalLights.length; i++)
            directionalLights[i] = new DirectionalLight();
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
        vertexShaderBuilder.addArrayUniformVariable("u_dirLights", numDirectionalLights, "DirectionalLight",
                new BaseShader.LocalSetter() {
                    @Override
                    public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                        final DirectionalLightsAttribute dla = combinedAttributes.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
                        final Array<DirectionalLight> dirs = dla == null ? null : dla.lights;

                        int dirLightsLoc = shader.getUniformID("u_dirLights[0].color");
                        int dirLightsSize = shader.getUniformID("u_dirLights[1].color") - dirLightsLoc;
                        int dirLightsDirectionOffset = shader.getUniformID("u_dirLights[0].direction") - dirLightsLoc;

                        for (int i = 0; i < directionalLights.length; i++) {
                            if (dirs == null || i >= dirs.size) {
                                if (directionalLights[i].color.r == 0f && directionalLights[i].color.g == 0f
                                        && directionalLights[i].color.b == 0f) continue;
                                directionalLights[i].color.set(0, 0, 0, 1);
                            } else if (directionalLights[i].equals(dirs.get(i)))
                                continue;
                            else
                                directionalLights[i].set(dirs.get(i));

                            int idx = dirLightsLoc + i * dirLightsSize;
                            shader.program.setUniformf(idx, directionalLights[i].color.r, directionalLights[i].color.g,
                                    directionalLights[i].color.b);
                            shader.program.setUniformf(idx + dirLightsDirectionOffset, directionalLights[i].direction.x,
                                    directionalLights[i].direction.y, directionalLights[i].direction.z);
                            if (dirLightsSize <= 0) break;
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
        function.append("void applyDirectionalLights(vec4 pos, vec3 diffuseLight, vec3 specularLight) {\n");
        if (hasSpecular)
            function.append("  vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);\n");
        function.append("  for (int i = 0; i < " + numDirectionalLights + "; i++) {\n" +
                "    vec3 lightDir = -u_dirLights[i].direction;\n" +
                "    float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);\n" +
                "    vec3 value = u_dirLights[i].color * NdotL;\n" +
                "    v_lightDiffuse += value;\n");
        if (hasSpecular) {
            function.append("    float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));\n" +
                    "    v_lightSpecular += value * pow(halfDotView, u_shininess);\n");
        }
        function.append("  }\n");
        function.append("}\n");

        vertexShaderBuilder.addFunction("applyDirectionalLights", function.toString());
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return numDirectionalLights > 0 && (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) > 0;
    }
}
