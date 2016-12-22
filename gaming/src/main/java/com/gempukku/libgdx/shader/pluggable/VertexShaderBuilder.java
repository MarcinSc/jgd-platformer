package com.gempukku.libgdx.shader.pluggable;

import com.gempukku.libgdx.shader.UniformRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

public class VertexShaderBuilder extends CommonShaderBuilder {
    private Map<String, String> attributeVariables = new LinkedHashMap<String, String>();

    public VertexShaderBuilder(UniformRegistry uniformRegistry) {
        super(uniformRegistry);
    }

    public void addAttributeVariable(String name, String type) {
        if (attributeVariables.containsKey(name))
            throw new IllegalStateException("Already contains vertex attribute of that name");
        attributeVariables.put(name, type);
    }

    private void appendAttributeVariables(StringBuilder stringBuilder) {
        for (Map.Entry<String, String> uniformDefinition : attributeVariables.entrySet()) {
            stringBuilder.append("attribute " + uniformDefinition.getValue() + " " + uniformDefinition.getKey() + ";\n");
        }
        if (!attributeVariables.isEmpty())
            stringBuilder.append("\n");
    }

    public String buildProgram(String mainBody) {
        StringBuilder result = new StringBuilder();

        appendStructures(result);
        appendAttributeVariables(result);
        appendUniformVariables(result);
        appendVaryingVariables(result);
        appendVariables(result);

        appendFunctions(result);

        result.append("void main() {\n");

        result.append(mainBody);

        result.append("}\n");
        return result.toString();
    }
}
