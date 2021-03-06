package jgd.platformer.gameplay.logic.signal.producer.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapManager;
import jgd.platformer.gameplay.logic.signal.SignalManager;

import java.awt.geom.Rectangle2D;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
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
        signalProducers = entityIndexManager.addIndexOnComponents(HitboxOverlapSignalProducerComponent.class, Location3DComponent.class);
    }

    @ReceiveEvent
    public void checkHitboxOverlap(GameLoopUpdate event, EntityRef entityRef) {
        for (EntityRef signalProducer : signalProducers) {
            Location3DComponent locationComp = signalProducer.getComponent(Location3DComponent.class);
            Vector3 location = locationComp.getLocation();
            HitboxOverlapSignalProducerComponent hitboxOverlapProducer = signalProducer.getComponent(HitboxOverlapSignalProducerComponent.class);
            Vector2 translate = hitboxOverlapProducer.getTranslate();
            Vector2 size = hitboxOverlapProducer.getSize();

            Rectangle2D.Float overlap = new Rectangle2D.Float(
                    location.x + translate.x,
                    location.y + translate.y,
                    size.x, size.y);

            int zLayer = MathUtils.floor(location.z);

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
