package com.gempukku.gaming.gdx.pluggable;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

public class UniformVariable {
    public final String type;
    public final BaseShader.Setter setter;

    public UniformVariable(String type, BaseShader.Setter setter) {
        this.type = type;
        this.setter = setter;
    }
}
