package com.gempukku.gaming.ai;

import com.gempukku.gaming.ai.map.MapAIReference;
import com.gempukku.secsy.entity.EntityRef;

public class EntityRefReference extends MapAIReference {
    private EntityRef entityRef;

    public EntityRefReference(EntityRef entityRef) {
        super(entityRef.getComponent(AIComponent.class).getValues());
        this.entityRef = entityRef;
    }

    public EntityRef getEntityRef() {
        return entityRef;
    }

    @Override
    public void storeValues() {
        entityRef.getComponent(AIComponent.class).setValues(getValues());
        entityRef.saveChanges();
    }
}
