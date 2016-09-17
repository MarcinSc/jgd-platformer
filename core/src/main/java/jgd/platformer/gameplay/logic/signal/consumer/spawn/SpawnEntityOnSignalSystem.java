package jgd.platformer.gameplay.logic.signal.consumer.spawn;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.logic.signal.SignalActivated;
import jgd.platformer.gameplay.logic.spawning.PlatformerEntitySpawner;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class SpawnEntityOnSignalSystem {
    @Inject
    private PlatformerEntitySpawner platformerEntitySpawner;
    @Inject
    private EntityManager entityManager;

    @ReceiveEvent
    public void signalReceived(SignalActivated signalActivated, EntityRef entity, SpawnEntityOnSignalComponent spawnEntityOnSignal, Location3DComponent locationComp) {
        Vector3 location = locationComp.getLocation();
        platformerEntitySpawner.createEntityFromRecipeAt(
                location.x + spawnEntityOnSignal.getDistanceX(),
                location.y + spawnEntityOnSignal.getDistanceY(),
                location.z + spawnEntityOnSignal.getDistanceZ(),
                spawnEntityOnSignal.getPrefabName());

        entityManager.destroyEntity(entity);
    }
}
