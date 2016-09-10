package com.gempukku.gaming.rendering.postprocess.pip;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

public interface PictureInPictureSourceComponent extends Component {
    Vector2 getLocation();

    Vector2 getSize();

    Color getFrameColor();
}
