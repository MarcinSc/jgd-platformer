package com.gempukku.gaming.rendering.postprocess.gamma;

import com.gempukku.secsy.entity.Component;

public interface GammaComponent extends Component {
    float getFactor();

    void setFactor(float factor);
}
