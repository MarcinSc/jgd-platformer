package jgd.platformer.rendering;

import com.gempukku.secsy.entity.Component;

public interface CameraBoundsComponent extends Component {
    float getMinX();

    float getMaxX();

    float getMinY();

    float getMaxY();
}