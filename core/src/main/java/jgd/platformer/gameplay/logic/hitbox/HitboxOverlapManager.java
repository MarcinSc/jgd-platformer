package jgd.platformer.gameplay.logic.hitbox;

import com.gempukku.secsy.entity.EntityRef;

import java.awt.geom.Rectangle2D;
import java.util.function.Predicate;

public interface HitboxOverlapManager {
    Iterable<EntityRef> findOverlappedEntities(Rectangle2D rectangle, int zLayer, Predicate<EntityRef> filter);
}
