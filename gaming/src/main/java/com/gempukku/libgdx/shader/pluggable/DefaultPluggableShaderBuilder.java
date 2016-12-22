package com.gempukku.libgdx.shader.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.LinkedList;
import java.util.List;

public class DefaultPluggableShaderBuilder implements PluggableShaderBuilder {
    private BitSetPluggableShaderFeatures tempShaderFeatures = new BitSetPluggableShaderFeatures();

    private List<PluggableRenderInitializerCall> renderInitializers = new LinkedList<>();

    private PluggableVertexFunctionCall positionSource;
    private List<PluggableVertexFunctionCall> positionProcessors = new LinkedList<PluggableVertexFunctionCall>();

    private PluggableFragmentFunctionCall colorSource;
    private List<PluggableFragmentFunctionCall> colorProcessors = new LinkedList<PluggableFragmentFunctionCall>();

    private List<PluggableVertexFunctionCall> additionalVertexCalls = new LinkedList<PluggableVertexFunctionCall>();
    private List<PluggableFragmentFunctionCall> additionalFragmentCalls = new LinkedList<PluggableFragmentFunctionCall>();

    public void addRenderInitializer(PluggableRenderInitializerCall renderInitializer) {
        renderInitializers.add(renderInitializer);
    }

    public void setPositionSource(PluggableVertexFunctionCall positionSource) {
        this.positionSource = positionSource;
    }

    public void setColorSource(PluggableFragmentFunctionCall colorSource) {
        this.colorSource = colorSource;
    }

    public void addPositionProcessor(PluggableVertexFunctionCall positionProcessor) {
        positionProcessors.add(positionProcessor);
    }

    public void addColorProcessor(PluggableFragmentFunctionCall colorProcessor) {
        colorProcessors.add(colorProcessor);
    }

    public void addAdditionalVertexCall(PluggableVertexFunctionCall additionalVertexCall) {
        additionalVertexCalls.add(additionalVertexCall);
    }

    public void addAdditionalFragmentCall(PluggableFragmentFunctionCall additionalFragmentCall) {
        additionalFragmentCalls.add(additionalFragmentCall);
    }

    @Override
    public void getShaderFeatures(Renderable renderable, PluggableShaderFeatures shaderFeatures) {
        for (PluggableRenderInitializerCall renderInitializer : renderInitializers) {
            if (renderInitializer.isProcessing(renderable))
                renderInitializer.appendShaderFeatures(renderable, shaderFeatures);
        }

        positionSource.appendShaderFeatures(renderable, shaderFeatures);
        for (PluggableVertexFunctionCall positionWrapper : positionProcessors) {
            if (positionWrapper.isProcessing(renderable))
                positionWrapper.appendShaderFeatures(renderable, shaderFeatures);
        }
        for (PluggableVertexFunctionCall additionalVertexCall : additionalVertexCalls) {
            if (additionalVertexCall.isProcessing(renderable))
                additionalVertexCall.appendShaderFeatures(renderable, shaderFeatures);
        }

        colorSource.appendShaderFeatures(renderable, shaderFeatures);
        for (PluggableFragmentFunctionCall colorWrapper : colorProcessors) {
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
        PluggableShader pluggableShader = new PluggableShader(renderable);

        for (PluggableRenderInitializerCall renderInitializer : renderInitializers) {
            if (renderInitializer.isProcessing(renderable))
                renderInitializer.appendInitializer(renderable, pluggableShader);
        }

        VertexShaderBuilder vertexShaderBuilder = new VertexShaderBuilder(pluggableShader);
        FragmentShaderBuilder fragmentShaderBuilder = new FragmentShaderBuilder(pluggableShader);

        String vertexProgram = createVertexProgram(renderable, vertexShaderBuilder);
        String fragmentProgram = createFragmentProgram(renderable, fragmentShaderBuilder);

        System.out.println(vertexProgram);
        System.out.println(fragmentProgram);
        pluggableShader.setProgram(new ShaderProgram(vertexProgram, fragmentProgram));
        return pluggableShader;
    }

    private String createVertexProgram(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        StringBuilder body = new StringBuilder();

        for (PluggableVertexFunctionCall additionalVertexCall : additionalVertexCalls) {
            if (additionalVertexCall.isProcessing(renderable)) {
                additionalVertexCall.appendFunction(renderable, vertexShaderBuilder);
                body.append("  " + additionalVertexCall.getFunctionName(renderable) + "();\n");
            }
        }
        if (!additionalVertexCalls.isEmpty())
            body.append("\n");

        String functionName = positionSource.getFunctionName(renderable);
        positionSource.appendFunction(renderable, vertexShaderBuilder);
        body.append("  vec4 position = " + functionName + "();\n");

        for (PluggableVertexFunctionCall positionWrapper : positionProcessors) {
            if (positionWrapper.isProcessing(renderable)) {
                positionWrapper.appendFunction(renderable, vertexShaderBuilder);
                body.append("  position = " + positionWrapper.getFunctionName(renderable) + "(position);\n");
            }
        }
        body.append("  gl_Position = position;\n");

        return vertexShaderBuilder.buildProgram(body.toString());
    }

    private String createFragmentProgram(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        StringBuilder body = new StringBuilder();

        for (PluggableFragmentFunctionCall additionalFragmentCall : additionalFragmentCalls) {
            if (additionalFragmentCall.isProcessing(renderable)) {
                additionalFragmentCall.appendFunction(renderable, fragmentShaderBuilder);
                body.append("  " + additionalFragmentCall.getFunctionName(renderable) + "();\n");
            }
        }
        if (!additionalFragmentCalls.isEmpty())
            body.append("\n");

        String functionName = colorSource.getFunctionName(renderable);
        colorSource.appendFunction(renderable, fragmentShaderBuilder);
        body.append("  vec4 color = " + functionName + "();\n");

        for (PluggableFragmentFunctionCall colorWrapper : colorProcessors) {
            if (colorWrapper.isProcessing(renderable)) {
                colorWrapper.appendFunction(renderable, fragmentShaderBuilder);
                body.append("  color = " + colorWrapper.getFunctionName(renderable) + "(color);\n");
            }
        }
        body.append("  gl_FragColor = color;\n");

        return fragmentShaderBuilder.buildProgram(body.toString());
    }
}
