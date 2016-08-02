package jgd.platformer.gameplay.logic.platform;

import com.gempukku.secsy.entity.Component;

public interface PlatformComponent extends Component {
    float getTranslateX();

    float getTranslateY();

    float getWidth();

    float getHeight();
}
