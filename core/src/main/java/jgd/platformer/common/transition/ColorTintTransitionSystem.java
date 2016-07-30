package jgd.platformer.common.transition;

import com.gempukku.gaming.rendering.postprocess.tint.color.ColorTintComponent;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;

@RegisterSystem
public class ColorTintTransitionSystem implements LifeCycleSystem {
    @Inject
    private TimeManager timeManager;
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex tintTransitionEntities;

    @Override
    public void initialize() {
        tintTransitionEntities = entityIndexManager.addIndexOnComponents(ColorTintComponent.class, ColorTintTransitionComponent.class);
    }

    @ReceiveEvent
    public void updateTint(GameLoopUpdate event, EntityRef entityRef) {
        long time = timeManager.getTime();
        for (EntityRef tintTransitionEntity : tintTransitionEntities) {
            ColorTintComponent tint = tintTransitionEntity.getComponent(ColorTintComponent.class);
            ColorTintTransitionComponent tintTransition = tintTransitionEntity.getComponent(ColorTintTransitionComponent.class);

            long startTime = tintTransition.getStartTime();
            long length = tintTransition.getLength();
            float factorFrom = tintTransition.getFactorFrom();
            float factorTo = tintTransition.getFactorTo();

            float factor;
            if (time <= startTime) {
                factor = factorFrom;
            } else if (startTime + length < time) {
                factor = factorTo;
                tintTransitionEntity.removeComponents(ColorTintTransitionComponent.class);
            } else {
                factor = factorFrom + (factorTo - factorFrom) * (time - startTime) / length;
            }
            tint.setFactor(factor);
            tintTransitionEntity.saveChanges();
        }
    }
}
