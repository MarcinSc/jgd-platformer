package jgd.platformer.gameplay.logic.ai.combat;

import com.gempukku.secsy.entity.Component;

public interface AttackProjectileComponent extends Component {
    String getProjectilePrefab();

    float getDistanceX();

    float getDistanceY();

    long getDissipateDuration();
}
