package com.gempukku.gaming.asset.component.handler;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.component.EntityComponentFieldTypeHandler;

public class Vector3Handler implements EntityComponentFieldTypeHandler<Vector3> {
    @Override
    public Vector3 copyFromEntity(Vector3 value) {
        return new Vector3(value);
    }

    @Override
    public Vector3 storeIntoEntity(Vector3 oldValue, Vector3 newValue) {
        if (oldValue != null) {
            oldValue.set(newValue);
            return oldValue;
        } else {
            return new Vector3(newValue);
        }
    }
}
