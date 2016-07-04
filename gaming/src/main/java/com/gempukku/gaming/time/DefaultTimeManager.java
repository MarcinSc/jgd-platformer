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
        EntityRef timeEntity = timeEntityProvider.getTimeEntity();
        TimeComponent time = timeEntity.getComponent(TimeComponent.class);

        if (time == null) {
            time = timeEntity.createComponent(TimeComponent.class);
            time.setTime(0);
            timeEntity.saveChanges();
        } else {
            long lastTime = time.getTime();
            timeSinceLastUpdate = timeDiff;
            time.setTime(lastTime + timeDiff);
            timeEntity.saveChanges();
        }
    }

    @Override
    public long getTime() {
        EntityRef timeEntity = timeEntityProvider.getTimeEntity();
        TimeComponent time = timeEntity.getComponent(TimeComponent.class);
        return time.getTime();
    }

    @Override
    public long getTimeSinceLastUpdate() {
        return timeSinceLastUpdate;
    }
}
