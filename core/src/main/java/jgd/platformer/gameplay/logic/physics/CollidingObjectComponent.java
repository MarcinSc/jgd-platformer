package jgd.platformer.gameplay.logic.physics;

import com.gempukku.secsy.entity.Component;

public interface CollidingObjectComponent extends Component {
    float getTranslateX();

    float getTranslateY();

    float getWidth();

    float getHeight();
}
