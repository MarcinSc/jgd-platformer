package com.gempukku.gaming.asset.component.handler;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.gaming.asset.component.EntityComponentFieldTypeHandler;

public class Vector2Handler implements EntityComponentFieldTypeHandler<Vector2> {
    @Override
    public Vector2 copyFromEntity(Vector2 value) {
        return new Vector2(value);
    }

    @Override
    public Vector2 storeIntoEntity(Vector2 oldValue, Vector2 newValue) {
        if (oldValue != null) {
            oldValue.set(newValue);
            return oldValue;
        } else {
            return new Vector2(newValue);
        }
    }
}
