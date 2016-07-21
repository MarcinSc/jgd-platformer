package jgd.platformer.gameplay.rendering;

import com.gempukku.secsy.entity.Component;

public interface ModelRotateComponent extends Component {
    float getRotateY();

    void setRotateY(float rotateY);
}
