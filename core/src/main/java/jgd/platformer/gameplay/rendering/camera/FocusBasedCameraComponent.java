package jgd.platformer.gameplay.rendering.camera;

import com.gempukku.secsy.entity.Component;

public interface FocusBasedCameraComponent extends Component {
    int getZ();

    float getDistanceY();
}
