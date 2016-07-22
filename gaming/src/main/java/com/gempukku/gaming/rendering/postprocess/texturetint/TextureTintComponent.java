package com.gempukku.gaming.rendering.postprocess.texturetint;

import com.gempukku.secsy.entity.Component;

public interface TextureTintComponent extends Component {
    String getTextureAtlasId();

    String getTextureName();

    float getFactor();

    void setFactor(float factor);
}
