package jgd.platformer.gameplay.component;

import com.gempukku.secsy.entity.Component;

public interface BaseLocationComponent extends Component {
    float getX();

    void setX(float x);

    float getY();

    void setY(float y);

    float getZ();

    void setZ(float z);
}
