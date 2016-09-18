package jgd.platformer.gameplay.logic.ai.combat;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface AttackProjectileComponent extends Component {
    Map<String, Object> getProjectileRecipe();

    Vector2 getDistance();

    long getDissipateDuration();

    long getProjectileShootFrequency();

    long getLastProjectileShot();

    void setLastProjectileShot(long lastProjectileShot);

    float getProjectileSpeed();
}
