package com.gempukku.gaming.asset.component.handler;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.gaming.asset.component.EntityComponentFieldTypeHandler;

public class ColorHandler implements EntityComponentFieldTypeHandler<Color> {
    @Override
    public Color copyFromEntity(Color value) {
        return new Color(value);
    }

    @Override
    public Color storeIntoEntity(Color oldValue, Color newValue) {
        if (oldValue != null) {
            oldValue.set(newValue);
            return oldValue;
        } else {
            return new Color(newValue);
        }
    }
}
