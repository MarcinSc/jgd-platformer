package com.gempukku.gaming.rendering.postprocess.tint.grain;

import com.gempukku.secsy.entity.Component;

public interface GrainComponent extends Component {
    float getGrainSize();

    void setGrainSize(float grainSize);

    float getFactor();

    void setFactor(float factor);
}
