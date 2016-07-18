package jgd.platformer.gameplay.logic.physics.gravity;

import com.gempukku.secsy.entity.Component;

public interface AffectedByGravityComponent extends Component {
    float getGravityMultiplier();
}
