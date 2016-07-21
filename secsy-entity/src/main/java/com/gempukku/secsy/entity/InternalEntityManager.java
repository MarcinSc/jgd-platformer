package com.gempukku.secsy.entity;

public interface InternalEntityManager {
    void addEntityEventListener(EntityEventListener entityEventListener);

    void removeEntityEventListener(EntityEventListener entityEventListener);

    void addEntityListener(EntityListener entityListener);

    void removeEntityListener(EntityListener entityListener);

    int getEntityId(EntityRef entityRef);

    EntityRef wrapEntityStub(SimpleEntity entity);
}
