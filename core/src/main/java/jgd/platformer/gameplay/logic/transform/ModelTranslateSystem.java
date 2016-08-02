package jgd.platformer.gameplay.logic.transform;

import com.badlogic.gdx.math.Interpolation;
import com.gempukku.gaming.interpolation.InterpolationUtil;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.BaseLocationComponent;
import jgd.platformer.gameplay.component.LocationComponent;

@RegisterSystem(
        profiles = "gameplay"
)
public class ModelTranslateSystem implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private EntityIndex translateEntities;

    @Override
    public void initialize() {
        translateEntities = entityIndexManager.addIndexOnComponents(ConstantModelTranslateComponent.class, LocationComponent.class, BaseLocationComponent.class);
    }

    @ReceiveEvent
    public void translateModels(GameLoopUpdate event, EntityRef entityRef) {
        long time = timeManager.getTime();

        for (EntityRef translateEntity : translateEntities) {
            ConstantModelTranslateComponent translate = translateEntity.getComponent(ConstantModelTranslateComponent.class);
            LocationComponent location = translateEntity.getComponent(LocationComponent.class);
            BaseLocationComponent baseLocation = translateEntity.getComponent(BaseLocationComponent.class);

            long baseTime = time - translate.getPhaseShift();

            int beforeTime = translate.getBeforeTime();
            int moveAwayTime = translate.getMoveAwayTime();
            int awayTime = translate.getAwayTime();
            int moveBackTime = translate.getMoveBackTime();

            int cycleTime = beforeTime + moveAwayTime
                    + awayTime + moveBackTime;

            long timeInCycle = baseTime % cycleTime;

            float a;
            if (timeInCycle < beforeTime) {
                a = 0;
            } else if (timeInCycle < beforeTime + moveAwayTime) {
                Interpolation interpolation = InterpolationUtil.getInterpolation(translate.getInterpolationAway());
                a = interpolation.apply(1f * (timeInCycle - beforeTime) / moveAwayTime);
            } else if (timeInCycle < beforeTime + moveAwayTime + awayTime) {
                a = 1;
            } else {
                Interpolation interpolation = InterpolationUtil.getInterpolation(translate.getInterpolationBack());
                a = 1f - interpolation.apply(1f * (timeInCycle - (beforeTime + moveAwayTime + awayTime)) / moveBackTime);
            }

            location.setX(baseLocation.getX() + a * translate.getDistanceX());
            location.setY(baseLocation.getY() + a * translate.getDistanceY());
            location.setZ(baseLocation.getZ() + a * translate.getDistanceZ());
            translateEntity.saveChanges();
        }
    }
}
