package jgd.platformer.gameplay.rendering.light;

import com.gempukku.secsy.entity.Component;

public interface PointLightComponent extends Component {
    float getTranslateX();

    float getTranslateY();

    float getTranslateZ();

    int getRed();

    int getGreen();

    int getBlue();

    float getIntensity();
}
