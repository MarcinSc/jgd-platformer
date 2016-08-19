package jgd.platformer.gameplay.rendering.model.g3d;

import com.gempukku.secsy.entity.Component;

public interface G3DModelAnimationComponent extends Component {
    String getPlayedAnimation();

    void setPlayedAnimation(String playedAnimation);
}
