package jgd.platformer.gameplay.logic.faction;

import com.gempukku.secsy.entity.EntityRef;

import java.util.function.Predicate;

public interface FactionManager {
    EntityRef findClosestEnemy(EntityRef entityRef, Predicate<EntityRef> entityPredicate);

    boolean hasEnemy(EntityRef entityRef, Predicate<EntityRef> entityPredicate);
}
