package jgd.platformer.gameplay.logic.ai;

import com.gempukku.secsy.entity.Component;

public interface FacingDirectionComponent extends Component {
    String getDirection();

    void setDirection(String direction);
}
