package jgd.platformer.logic.physics;

import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface CollisionObstacleComponent extends Component {
    float getTranslateX();

    float getTranslateY();

    float getWidth();

    float getHeight();

    List<String> getCollideSides();
}
