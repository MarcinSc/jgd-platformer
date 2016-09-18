package jgd.platformer.gameplay.logic.physics;

import jgd.platformer.common.component.Rectangle2DComponent;

import java.util.List;

public interface CollisionObstacleComponent extends Rectangle2DComponent {
    List<String> getCollideSides();
}
