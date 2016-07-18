package jgd.platformer.gameplay.rendering;

import com.gempukku.secsy.entity.Component;

public interface CameraFocusComponent extends Component {
    float getFocusWeight();

    void setFocusWeight(float focusWeight);
}
