package jgd.platformer.gameplay;

import com.gempukku.gaming.time.TimeEntityProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;

@RegisterSystem(
        profiles = "gameplay",
        shared = TimeEntityProvider.class
)
public class PlatformerTimeEntityProvider implements TimeEntityProvider {
    @Inject
    private EntityManager entityManager;

    private EntityRef timeEntity;

    @Override
    public EntityRef getTimeEntity() {
        if (timeEntity == null || !timeEntity.exists())
            timeEntity = entityManager.createEntity();
        return timeEntity;
    }
}
