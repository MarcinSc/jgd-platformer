package jgd.platformer.gameplay.rendering.model.g3d;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface G3DModelColorComponent extends Component {
    String getMaterialId();

    Color getColor();
}
