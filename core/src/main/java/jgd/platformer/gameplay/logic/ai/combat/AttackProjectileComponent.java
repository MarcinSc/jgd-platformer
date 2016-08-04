package jgd.platformer.gameplay.logic.ai.combat;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface AttackProjectileComponent extends Component {
    Map<String, Object> getProjectileRecipe();

    float getDistanceX();

    float getDistanceY();

    long getDissipateDuration();

    long getProjectileShootFrequency();

    long getLastProjectileShot();

    void setLastProjectileShot(long lastProjectileShot);

    float getProjectileSpeed();
}
