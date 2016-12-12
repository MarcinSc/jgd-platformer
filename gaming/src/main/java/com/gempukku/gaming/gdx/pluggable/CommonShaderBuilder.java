package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CommonShaderBuilder {
    private UniformRegistry uniformRegistry;

    private Map<String, UniformVariable> uniformVariables = new LinkedHashMap<String, UniformVariable>();
    private Map<String, String> varyingVariables = new LinkedHashMap<String, String>();
    private Map<String, String> variables = new LinkedHashMap<>();
    private Map<String, String> functions = new LinkedHashMap<String, String>();

    public CommonShaderBuilder(UniformRegistry uniformRegistry) {
        this.uniformRegistry = uniformRegistry;
    }

    public void addArrayUniformVariable(String name, int size, String type, BaseShader.Setter setter) {
        if (uniformVariables.containsKey(name))
            throw new IllegalStateException("Already contains uniform of that name");
        uniformRegistry.registerUniform(name, setter);
        uniformVariables.put(name + "[" + size + "]", new UniformVariable(type, setter));
    }

    public void addUniformVariable(String name, String type, BaseShader.Setter setter) {
        UniformVariable uniformVariable = uniformVariables.get(name);
        if (uniformVariable != null && !uniformVariable.type.equals(type) && uniformVariable.setter != setter)
            throw new IllegalStateException("Already contains uniform of that name with a different setter or type");

        if (uniformVariable == null) {
            uniformRegistry.registerUniform(name, setter);
            uniformVariables.put(name, new UniformVariable(type, setter));
        }
    }

    public void addVaryingVariable(String name, String type) {
        if (varyingVariables.containsKey(name))
            throw new IllegalStateException("Already contains varying variable of that name");
        varyingVariables.put(name, type);
    }

    public void addVariable(String name, String type) {
        if (variables.containsKey(name))
            throw new IllegalStateException("Already contains variable of that name");
        variables.put(name, type);
    }

    public void addFunction(String name, String functionText) {
        if (functions.containsKey(name))
            throw new IllegalStateException("Already contains function of that name");
        functions.put(name, functionText);
    }

    protected void appendUniformVariables(StringBuilder stringBuilder) {
        for (Map.Entry<String, UniformVariable> uniformDefinition : uniformVariables.entrySet()) {
            stringBuilder.append("uniform " + uniformDefinition.getValue().type + " " + uniformDefinition.getKey() + ";\n");
        }
        if (!uniformVariables.isEmpty())
            stringBuilder.append("\n");
    }

    protected void appendVaryingVariables(StringBuilder stringBuilder) {
        for (Map.Entry<String, String> varyingDefinition : varyingVariables.entrySet()) {
            stringBuilder.append("varying " + varyingDefinition.getValue() + " " + varyingDefinition.getKey() + ";\n");
        }
        if (!varyingVariables.isEmpty())
            stringBuilder.append("\n");
    }

    protected void appendVariables(StringBuilder stringBuilder) {
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            stringBuilder.append(variable.getValue() + " " + variable.getKey() + ";\n");
        }
        if (!variables.isEmpty())
            stringBuilder.append("\n");
    }

    protected void appendFunctions(StringBuilder stringBuilder) {
        for (String function : functions.values()) {
            stringBuilder.append(function);
            stringBuilder.append("\n");
        }
    }
}
