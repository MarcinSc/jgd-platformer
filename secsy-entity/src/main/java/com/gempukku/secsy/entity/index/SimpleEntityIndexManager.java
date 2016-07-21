package com.gempukku.secsy.entity.index;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.*;

import java.util.HashSet;
import java.util.Set;

@RegisterSystem(
        profiles = "simpleEntityIndexManager",
        shared = EntityIndexManager.class)
public class SimpleEntityIndexManager implements EntityIndexManager, EntityRefCreationCallback, EntityListener,
        LifeCycleSystem {
    @Inject
    private InternalEntityManager internalEntityManager;

    private Set<ComponentEntityIndex> indices = new HashSet<>();

    @Override
    public void initialize() {
        internalEntityManager.addEntityListener(this);
    }

    @Override
    public EntityIndex addIndexOnComponents(Class<? extends Component>... components) {
        ComponentEntityIndex index = new ComponentEntityIndex(this, components);
        indices.add(index);
        return index;
    }

    @Override
    public EntityRef createEntityRef(SimpleEntity entity) {
        return internalEntityManager.wrapEntityStub(entity);
    }

    @Override
    public void entitiesModified(Iterable<SimpleEntity> entity) {
        for (ComponentEntityIndex index : indices) {
            index.entitiesModified(entity);
        }
    }
}
