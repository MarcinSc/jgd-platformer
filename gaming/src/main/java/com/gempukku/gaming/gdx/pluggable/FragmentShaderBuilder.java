package com.gempukku.gaming.gdx.pluggable;

import java.util.List;

public class FragmentShaderBuilder extends CommonShaderBuilder {
    public FragmentShaderBuilder(UniformRegistry uniformRegistry) {
        super(uniformRegistry);
    }

    public String buildProgram(List<String> additionalFunctionCalls, String executionChain) {
        StringBuilder result = new StringBuilder();

        appendUniformVariables(result);
        appendVaryingVariables(result);
        appendVariables(result);

        appendFunctions(result);

        result.append("void main() {\n");

        for (String additionalFunctionCall : additionalFunctionCalls) {
            result.append("  " + additionalFunctionCall + "();\n");
        }

        result.append("  gl_FragColor = " + executionChain + ";\n");

        result.append("}\n");
        return result.toString();
    }
}
