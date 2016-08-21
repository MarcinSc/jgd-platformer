package jgd.platformer.gameplay.logic.signal.producer.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapManager;
import jgd.platformer.gameplay.logic.signal.SignalManager;

import java.awt.geom.Rectangle2D;

@RegisterSystem(
        profiles = "gameplay"
)
public class HitboxOverlapSignalProducerSystem implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private HitboxOverlapManager hitboxOverlapManager;
    @Inject
    private SignalManager signalManager;

    private EntityIndex signalProducers;

    @Override
    public void initialize() {
        signalProducers = entityIndexManager.addIndexOnComponents(HitboxOverlapSignalProducerComponent.class, LocationComponent.class);
    }

    @ReceiveEvent
    public void checkHitboxOverlap(GameLoopUpdate event, EntityRef entityRef) {
        for (EntityRef signalProducer : signalProducers) {
            LocationComponent location = signalProducer.getComponent(LocationComponent.class);
            HitboxOverlapSignalProducerComponent hitboxOverlapProducer = signalProducer.getComponent(HitboxOverlapSignalProducerComponent.class);

            Rectangle2D.Float overlap = new Rectangle2D.Float(
                    location.getX() + hitboxOverlapProducer.getTranslateX(),
                    location.getY() + hitboxOverlapProducer.getTranslateY(),
                    hitboxOverlapProducer.getWidth(), hitboxOverlapProducer.getHeight());

            int zLayer = MathUtils.floor(location.getZ());

            Iterable<EntityRef> overlapping = hitboxOverlapManager.findOverlappedEntities(overlap, zLayer,
                    entity -> entity.hasComponent(ActivateHitboxOverlapSignalProducerComponent.class));

            if (overlapping.iterator().hasNext()) {
                signalManager.signalActivated(signalProducer);
            } else {
                signalManager.signalDeactivated(signalProducer);
            }
        }
    }
}
