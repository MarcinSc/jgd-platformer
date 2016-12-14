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
import com.gempukku.gaming.gdx.pluggable.VertexShaderBuilder;

public class ApplyPointLights implements PerVertexLightingFunctionCall {
    private int numPointLights;
    // Separate due to config
    private PluggableShaderFeatureRegistry.PluggableShaderFeature applyPointLights = PluggableShaderFeatureRegistry.registerFeature();

    private PointLight[] pointLights;

    public ApplyPointLights(int numPointLights) {
        this.numPointLights = numPointLights;
        this.pointLights = new PointLight[numPointLights];
        for (int i = 0; i < pointLights.length; i++)
            pointLights[i] = new PointLight();
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applyPointLights";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(applyPointLights);
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder, boolean hasSpecular) {
        vertexShaderBuilder.addStructure("PointLight",
                "  vec3 color;\n" +
                        "  vec3 position;\n");
        vertexShaderBuilder.addArrayUniformVariable("u_pointLights", numPointLights, "PointLight",
                new BaseShader.LocalSetter() {
                    @Override
                    public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                        final PointLightsAttribute pla = combinedAttributes.get(PointLightsAttribute.class, PointLightsAttribute.Type);
                        final Array<PointLight> points = pla == null ? null : pla.lights;

                        int pointLightsLoc = shader.getUniformID("u_pointLights[0].color");
                        int pointLightsSize = shader.getUniformID("u_pointLights[1].color") - pointLightsLoc;
                        int pointLightsPositionOffset = shader.getUniformID("u_pointLights[0].position") - pointLightsLoc;
                        int pointLightsIntensityOffset = shader.getUniformID("u_pointLights[0].intensity") - pointLightsLoc;

                        for (int i = 0; i < pointLights.length; i++) {
                            if (points == null || i >= points.size) {
                                if (pointLights[i].intensity == 0f) continue;
                                pointLights[i].intensity = 0f;
                            } else if (pointLights[i].equals(points.get(i)))
                                continue;
                            else
                                pointLights[i].set(points.get(i));

                            int idx = pointLightsLoc + i * pointLightsSize;
                            shader.program.setUniformf(idx, pointLights[i].color.r * pointLights[i].intensity,
                                    pointLights[i].color.g * pointLights[i].intensity, pointLights[i].color.b * pointLights[i].intensity);
                            shader.program.setUniformf(idx + pointLightsPositionOffset, pointLights[i].position.x, pointLights[i].position.y,
                                    pointLights[i].position.z);
                            if (pointLightsIntensityOffset >= 0)
                                shader.program.setUniformf(idx + pointLightsIntensityOffset, pointLights[i].intensity);
                            if (pointLightsSize <= 0) break;
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
        function.append("void applyPointLights(vec4 pos, vec3 diffuseLight, vec3 specularLight) {\n");
        if (hasSpecular)
            function.append("  vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);\n");
        function.append("  for (int i = 0; i < " + numPointLights + "; i++) {\n" +
                "    vec3 lightDir = u_pointLights[i].position - pos.xyz;\n" +
                "    float dist2 = dot(lightDir, lightDir);\n" +
                "    lightDir *= inversesqrt(dist2);\n" +
                "    float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);\n" +
                "    vec3 value = u_pointLights[i].color * (NdotL / (1.0 + dist2));\n" +
                "    v_lightDiffuse += value;\n");
        if (hasSpecular) {
            function.append("    float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));\n" +
                    "    v_lightSpecular += value * pow(halfDotView, u_shininess);\n");
        }
        function.append("  }\n");
        function.append("}\n");

        vertexShaderBuilder.addFunction("applyPointLights", function.toString());
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return numPointLights > 0 && (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) > 0;
    }
}
