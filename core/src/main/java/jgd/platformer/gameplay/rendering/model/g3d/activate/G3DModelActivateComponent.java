package jgd.platformer.gameplay.rendering.model.g3d.activate;

import com.gempukku.secsy.entity.Component;

public interface G3DModelActivateComponent extends Component {
    boolean isActivated();

    void setActivated(boolean activated);

    String getActivateAnimation();

    default float getActivateAnimationSpeed() {
        return 1;
    }

    String getDeactivateAnimation();

    default float getDeactivateAnimationSpeed() {
        return 1;
    }
}
