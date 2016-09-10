package com.gempukku.gaming.asset.component.converter;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.gaming.asset.component.ComponentFieldTypeConverter;

public class Vector2Converter implements ComponentFieldTypeConverter<Vector2> {
    @Override
    public String convertFrom(Vector2 value) {
        return value.x + "," + value.y;
    }

    @Override
    public Vector2 convertTo(String value) {
        String[] split = value.split(",");
        return new Vector2(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
    }
}
