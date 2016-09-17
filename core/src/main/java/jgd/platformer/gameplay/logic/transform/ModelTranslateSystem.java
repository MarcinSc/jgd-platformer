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
import jgd.platformer.gameplay.component.BaseLocation3DComponent;
import jgd.platformer.gameplay.component.Location3DComponent;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class ModelTranslateSystem implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private EntityIndex translateEntities;
    private EntityIndex translateOverTimeEntities;

    @Override
    public void initialize() {
        translateEntities = entityIndexManager.addIndexOnComponents(ConstantModelTranslateComponent.class, Location3DComponent.class, BaseLocation3DComponent.class);
        translateOverTimeEntities = entityIndexManager.addIndexOnComponents(ModelTranslateOverTimeComponent.class, Location3DComponent.class);
    }

    @ReceiveEvent
    public void translateModels(GameLoopUpdate event, EntityRef entityRef) {
        long time = timeManager.getTime();

        for (EntityRef translateEntity : translateEntities) {
            ConstantModelTranslateComponent translate = translateEntity.getComponent(ConstantModelTranslateComponent.class);
            Location3DComponent location = translateEntity.getComponent(Location3DComponent.class);
            BaseLocation3DComponent baseLocation = translateEntity.getComponent(BaseLocation3DComponent.class);

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

            location.setLocation(baseLocation.getLocation().add(translate.getDistance().scl(a)));
            translateEntity.saveChanges();
        }

        for (EntityRef translateOverTimeEntity : translateOverTimeEntities) {
            ModelTranslateOverTimeComponent translate = translateOverTimeEntity.getComponent(ModelTranslateOverTimeComponent.class);
            Location3DComponent location = translateOverTimeEntity.getComponent(Location3DComponent.class);

            long progress = time - translate.getStartTime();
            long moveTime = translate.getMoveTime();

            float a;
            if (progress < 0) {
                a = 0;
            } else if (progress < moveTime) {
                Interpolation interpolation = InterpolationUtil.getInterpolation(translate.getInterpolation());
                a = interpolation.apply(1f * progress / moveTime);
            } else {
                translateOverTimeEntity.removeComponents(ModelTranslateOverTimeComponent.class);
                a = 1;
            }
            location.setLocation(translate.getSource().add(translate.getDestination().sub(translate.getSource()).scl(a)));
            translateOverTimeEntity.saveChanges();
        }

    }
}
