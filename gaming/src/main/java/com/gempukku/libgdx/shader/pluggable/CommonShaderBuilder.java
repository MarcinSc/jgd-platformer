package com.gempukku.libgdx.shader.pluggable;

import com.gempukku.libgdx.shader.UniformRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CommonShaderBuilder {
    private UniformRegistry uniformRegistry;

    private Map<String, UniformVariable> uniformVariables = new LinkedHashMap<>();
    private Map<String, String> varyingVariables = new LinkedHashMap<>();
    private Map<String, String> variables = new LinkedHashMap<>();
    private Map<String, String> functions = new LinkedHashMap<>();
    private Map<String, String> structures = new LinkedHashMap<>();

    public CommonShaderBuilder(UniformRegistry uniformRegistry) {
        this.uniformRegistry = uniformRegistry;
    }

    public void addStructArrayUniformVariable(String name, String[] fieldNames, int size, String type, boolean global, UniformRegistry.StructArrayUniformSetter setter) {
        if (uniformVariables.containsKey(name))
            throw new IllegalStateException("Already contains uniform of that name");
        uniformRegistry.registerStructArrayUniform(name, fieldNames, global, setter);
        uniformVariables.put(name + "[" + size + "]", new UniformVariable(type, global, null));
    }

    public void addArrayUniformVariable(String name, int size, String type, boolean global, UniformRegistry.UniformSetter setter) {
        if (uniformVariables.containsKey(name))
            throw new IllegalStateException("Already contains uniform of that name");
        uniformRegistry.registerUniform(name, global, setter);
        uniformVariables.put(name + "[" + size + "]", new UniformVariable(type, global, setter));
    }

    public void addUniformVariable(String name, String type, boolean global, UniformRegistry.UniformSetter setter) {
        UniformVariable uniformVariable = uniformVariables.get(name);
        if (uniformVariable != null &&
                (!uniformVariable.type.equals(type) || uniformVariable.setter != setter || uniformVariable.global != global))
            throw new IllegalStateException("Already contains uniform of that name with a different setter, type or global flag");

        if (uniformVariable == null) {
            uniformRegistry.registerUniform(name, global, setter);
            uniformVariables.put(name, new UniformVariable(type, global, setter));
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

    public void addStructure(String name, String structureText) {
        if (structures.containsKey(name))
            throw new IllegalStateException("Already contains structure of that name");
        structures.put(name, structureText);
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

    protected void appendStructures(StringBuilder stringBuilder) {
        for (Map.Entry<String, String> structureEntry : structures.entrySet()) {
            stringBuilder.append("struct " + structureEntry.getKey() + "\n" +
                    "{\n").append(structureEntry.getValue()).append("};\n");
        }
        if (!structures.isEmpty())
            stringBuilder.append("\n");
    }

    private class UniformVariable {
        public final String type;
        public final boolean global;
        public final UniformRegistry.UniformSetter setter;

        public UniformVariable(String type, boolean global, UniformRegistry.UniformSetter setter) {
            this.type = type;
            this.global = global;
            this.setter = setter;
        }
    }
}
