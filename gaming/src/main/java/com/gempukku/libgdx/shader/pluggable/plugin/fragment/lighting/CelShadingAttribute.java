package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;

public class CelShadingAttribute extends IntAttribute {
    public static final String CelShadingAlias = "celShading";
    public static final long CelShading = register(CelShadingAlias);

    public static CelShadingAttribute createCelShadingAttribute(int celShadingCount) {
        return new CelShadingAttribute(celShadingCount);
    }

    private CelShadingAttribute(int celShadingCount) {
        super(CelShading, celShadingCount);
    }

    @Override
    public Attribute copy() {
        return new CelShadingAttribute(value);
    }
}
