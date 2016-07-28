package jgd.platformer.gameplay.logic.faction;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapEvent;

import java.util.List;

@RegisterSystem(
        profiles = "gameplay"
)
public class FactionSystem implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex factionEntities;

    @Override
    public void initialize() {
        factionEntities = entityIndexManager.addIndexOnComponents(FactionComponent.class);
    }

    @ReceiveEvent
    public void enemyFactionOverlap(HitboxOverlapEvent event, EntityRef entityRef, FactionMemberComponent factionMember) {
        EntityRef otherEntity = event.getOtherEntity();
        if (otherEntity.hasComponent(FactionMemberComponent.class)) {
            String faction = factionMember.getFactionName();
            String otherFaction = otherEntity.getComponent(FactionMemberComponent.class).getFactionName();

            if (areEnemies(faction, otherFaction)) {
                entityRef.send(new EnemyOverlapEvent(otherEntity));
            }
        }
    }

    private boolean areEnemies(String faction, String otherFaction) {
        return getEnemies(faction).contains(otherFaction) || getEnemies(otherFaction).contains(faction);
    }

    private List<String> getEnemies(String faction) {
        return getFaction(faction).getEnemies();
    }

    private FactionComponent getFaction(String factionName) {
        for (EntityRef factionEntity : factionEntities) {
            FactionComponent faction = factionEntity.getComponent(FactionComponent.class);
            if (faction.getName().equals(factionName))
                return faction;
        }
        return null;
    }
}
