package jgd.platformer.gameplay.rendering.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

public interface PointLightComponent extends Component {
    Vector3 getTranslate();

    Color getColor();

    float getIntensity();
}
