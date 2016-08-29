package jgd.platformer.gameplay.logic.faction;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.logic.hitbox.HitboxOverlapEvent;

import java.util.List;
import java.util.function.Predicate;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}, shared = FactionManager.class
)
public class FactionSystem implements LifeCycleSystem, FactionManager {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex factionEntities;
    private EntityIndex factionMemberEntities;

    @Override
    public void initialize() {
        factionEntities = entityIndexManager.addIndexOnComponents(FactionComponent.class);
        factionMemberEntities = entityIndexManager.addIndexOnComponents(FactionMemberComponent.class);
    }

    @ReceiveEvent
    public void enemyFactionOverlap(HitboxOverlapEvent event, EntityRef entityRef, FactionMemberComponent factionMember) {
        EntityRef otherEntity = event.getOtherEntity();
        if (otherEntity.hasComponent(FactionMemberComponent.class)) {
            String faction = factionMember.getFactionName();
            String otherFaction = getFaction(otherEntity);

            if (areEnemies(faction, otherFaction)) {
                entityRef.send(new EnemyOverlapEvent(otherEntity));
            }
        }
    }

    private String getFaction(EntityRef entity) {
        return entity.getComponent(FactionMemberComponent.class).getFactionName();
    }

    @Override
    public boolean hasEnemy(EntityRef entityRef, Predicate<EntityRef> entityPredicate) {
        String faction = getFaction(entityRef);
        List<String> enemies = getEnemies(faction);

        for (EntityRef otherEntity : factionMemberEntities) {
            String otherFaction = getFaction(otherEntity);
            if (enemies.contains(otherFaction) && entityPredicate.test(otherEntity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EntityRef findClosestEnemy(EntityRef entityRef, Predicate<EntityRef> entityPredicate) {
        LocationComponent location = entityRef.getComponent(LocationComponent.class);
        float x = location.getX();
        float y = location.getY();
        String faction = getFaction(entityRef);
        List<String> enemies = getEnemies(faction);

        float lowestDistance = Float.MAX_VALUE;
        EntityRef result = null;
        for (EntityRef otherEntity : factionMemberEntities) {
            String otherFaction = getFaction(otherEntity);
            if (enemies.contains(otherFaction) && entityPredicate.test(otherEntity)) {
                LocationComponent otherLocation = otherEntity.getComponent(LocationComponent.class);
                float otherX = otherLocation.getX();
                float otherY = otherLocation.getY();

                float xSquare = (otherX - x) * (otherX - x);
                float ySquare = (otherY - y) * (otherY - y);
                float distance = (float) Math.sqrt(xSquare + ySquare);

                if (distance < lowestDistance) {
                    result = otherEntity;
                }
            }
        }

        return result;
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
