package com.gempukku.gaming.time;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;

@RegisterSystem(
        profiles = "time", shared = {TimeManager.class, InternalTimeManager.class})
public class DefaultTimeManager implements TimeManager, InternalTimeManager {
    @Inject
    private TimeEntityProvider timeEntityProvider;

    private long timeSinceLastUpdate = 0;

    @Override
    public void updateTime(long timeDiff) {
        EntityRef worldEntity = timeEntityProvider.getTimeEntity();
        TimeComponent world = worldEntity.getComponent(TimeComponent.class);
        long lastTime = world.getTime();
        timeSinceLastUpdate = timeDiff;
        world.setTime(lastTime + timeDiff);
        worldEntity.saveChanges();
    }

    @Override
    public long getTime() {
        EntityRef worldEntity = timeEntityProvider.getTimeEntity();
        TimeComponent world = worldEntity.getComponent(TimeComponent.class);
        return world.getTime();
    }

    @Override
    public long getTimeSinceLastUpdate() {
        return timeSinceLastUpdate;
    }
}
