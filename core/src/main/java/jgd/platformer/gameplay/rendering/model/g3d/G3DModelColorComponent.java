package jgd.platformer.gameplay.rendering.model.g3d;

import com.gempukku.secsy.entity.Component;

public interface G3DModelColorComponent extends Component {
    String getMaterialId();

    int getRed();

    int getGreen();

    int getBlue();
}
