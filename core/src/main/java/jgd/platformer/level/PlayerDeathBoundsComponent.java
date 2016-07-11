package jgd.platformer.level;

import com.gempukku.secsy.entity.Component;

public interface PlayerDeathBoundsComponent extends Component {
    float getMinY();

    float getMinX();

    float getMaxX();
}
