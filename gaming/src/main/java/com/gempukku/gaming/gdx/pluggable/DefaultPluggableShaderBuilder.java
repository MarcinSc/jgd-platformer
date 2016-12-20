package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.LinkedList;
import java.util.List;

public class DefaultPluggableShaderBuilder implements PluggableShaderBuilder {
    private BitSetPluggableShaderFeatures tempShaderFeatures = new BitSetPluggableShaderFeatures();

    private PluggableVertexFunctionCall positionSource;
    private List<PluggableVertexFunctionCall> positionWrappers = new LinkedList<PluggableVertexFunctionCall>();

    private PluggableFragmentFunctionCall colorSource;
    private List<PluggableFragmentFunctionCall> colorWrappers = new LinkedList<PluggableFragmentFunctionCall>();

    private List<PluggableVertexFunctionCall> additionalVertexCalls = new LinkedList<PluggableVertexFunctionCall>();
    private List<PluggableFragmentFunctionCall> additionalFragmentCalls = new LinkedList<PluggableFragmentFunctionCall>();

    public void setPositionSource(PluggableVertexFunctionCall positionSource) {
        this.positionSource = positionSource;
    }

    public void setColorSource(PluggableFragmentFunctionCall colorSource) {
        this.colorSource = colorSource;
    }

    public void addPositionWrapper(PluggableVertexFunctionCall positionWrapper) {
        positionWrappers.add(positionWrapper);
    }

    public void addColorWrapper(PluggableFragmentFunctionCall colorWrapper) {
        colorWrappers.add(colorWrapper);
    }

    public void addAdditionalVertexCall(PluggableVertexFunctionCall additionalVertexCall) {
        additionalVertexCalls.add(additionalVertexCall);
    }

    public void addAdditionalFragmentCall(PluggableFragmentFunctionCall additionalFragmentCall) {
        additionalFragmentCalls.add(additionalFragmentCall);
    }

    @Override
    public void getShaderFeatures(Renderable renderable, PluggableShaderFeatures shaderFeatures) {
        StringBuilder builder = new StringBuilder();
        positionSource.appendShaderFeatures(renderable, shaderFeatures);
        for (PluggableVertexFunctionCall positionWrapper : positionWrappers) {
            if (positionWrapper.isProcessing(renderable))
                positionWrapper.appendShaderFeatures(renderable, shaderFeatures);
        }
        for (PluggableVertexFunctionCall additionalVertexCall : additionalVertexCalls) {
            if (additionalVertexCall.isProcessing(renderable))
                additionalVertexCall.appendShaderFeatures(renderable, shaderFeatures);
        }

        colorSource.appendShaderFeatures(renderable, shaderFeatures);
        for (PluggableFragmentFunctionCall colorWrapper : colorWrappers) {
            if (colorWrapper.isProcessing(renderable))
                colorWrapper.appendShaderFeatures(renderable, shaderFeatures);
        }
        for (PluggableFragmentFunctionCall additionalFragmentCall : additionalFragmentCalls) {
            if (additionalFragmentCall.isProcessing(renderable))
                additionalFragmentCall.appendShaderFeatures(renderable, shaderFeatures);
        }
    }

    @Override
    public PluggableShader buildShader(Renderable renderable) {
        DefaultUniformRegistry uniformRegistry = new DefaultUniformRegistry();

        VertexShaderBuilder vertexShaderBuilder = new VertexShaderBuilder(uniformRegistry);
        FragmentShaderBuilder fragmentShaderBuilder = new FragmentShaderBuilder(uniformRegistry);

        String vertexProgram = createVertexProgram(renderable, vertexShaderBuilder);
        String fragmentProgram = createFragmentProgram(renderable, fragmentShaderBuilder);

        System.out.println(vertexProgram);
        System.out.println(fragmentProgram);
        PluggableShader pluggableShader = new PluggableShader(new ShaderProgram(vertexProgram, fragmentProgram), renderable, uniformRegistry);
        return pluggableShader;
    }

    private String createVertexProgram(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        String functionName = positionSource.getFunctionName(renderable);
        positionSource.appendFunction(renderable, vertexShaderBuilder);
        String executionChain = functionName + "()";

        for (PluggableVertexFunctionCall positionWrapper : positionWrappers) {
            if (positionWrapper.isProcessing(renderable)) {
                positionWrapper.appendFunction(renderable, vertexShaderBuilder);
                executionChain = positionWrapper.getFunctionName(renderable) + "(" + executionChain + ")";
            }
        }

        List<String> vertexFunctions = new LinkedList<String>();
        for (PluggableVertexFunctionCall additionalVertexCall : additionalVertexCalls) {
            if (additionalVertexCall.isProcessing(renderable)) {
                additionalVertexCall.appendFunction(renderable, vertexShaderBuilder);
                vertexFunctions.add(additionalVertexCall.getFunctionName(renderable));
            }
        }

        return vertexShaderBuilder.buildProgram(vertexFunctions, executionChain);
    }

    private String createFragmentProgram(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        String functionName = colorSource.getFunctionName(renderable);
        colorSource.appendFunction(renderable, fragmentShaderBuilder);
        String executionChain = functionName + "()";

        for (PluggableFragmentFunctionCall colorWrapper : colorWrappers) {
            if (colorWrapper.isProcessing(renderable)) {
                colorWrapper.appendFunction(renderable, fragmentShaderBuilder);
                executionChain = colorWrapper.getFunctionName(renderable) + "(" + executionChain + ")";
            }
        }

        List<String> fragmentFunctions = new LinkedList<String>();
        for (PluggableFragmentFunctionCall additionalFragmentCall : additionalFragmentCalls) {
            if (additionalFragmentCall.isProcessing(renderable)) {
                additionalFragmentCall.appendFunction(renderable, fragmentShaderBuilder);
                fragmentFunctions.add(additionalFragmentCall.getFunctionName(renderable));
            }
        }

        return fragmentShaderBuilder.buildProgram(fragmentFunctions, executionChain);
    }
}
