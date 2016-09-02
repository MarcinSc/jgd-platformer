package com.gempukku.gaming.asset.component.converter;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.component.ComponentFieldTypeConverter;

public class Vector3Converter implements ComponentFieldTypeConverter<Vector3> {
    @Override
    public String convertFrom(Vector3 value) {
        return value.x + "," + value.y + "," + value.z;
    }

    @Override
    public Vector3 convertTo(String value) {
        String[] split = value.split(",");
        return new Vector3(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
    }
}
