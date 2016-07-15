package jgd.platformer.gameplay;

import com.gempukku.gaming.time.TimeEntityProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;

@RegisterSystem(
        profiles = "gameplay",
        shared = TimeEntityProvider.class
)
public class PlatformerTimeEntityProvider implements TimeEntityProvider, LifeCycleSystem {
    @Inject
    private EntityManager entityManager;

    private EntityRef timeEntity;

    @Override
    public void initialize() {
        timeEntity = entityManager.createEntity();
    }

    @Override
    public EntityRef getTimeEntity() {
        return timeEntity;
    }
}
