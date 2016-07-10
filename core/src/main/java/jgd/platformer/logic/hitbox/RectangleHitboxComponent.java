package jgd.platformer.logic.hitbox;

import com.gempukku.secsy.entity.Component;

public interface RectangleHitboxComponent extends Component {
    float getTranslateX();

    float getTranslateY();

    float getWidth();

    float getHeight();
}
