package jgd.platformer.gameplay.logic.ai.movement;

import com.gempukku.secsy.entity.Component;

public interface AIMovementConfigurationComponent extends Component {
    float getMovementVelocity();
}
