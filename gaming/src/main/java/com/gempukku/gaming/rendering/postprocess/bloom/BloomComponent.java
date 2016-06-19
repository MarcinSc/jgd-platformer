package com.gempukku.gaming.rendering.postprocess.bloom;

import com.gempukku.gaming.component.ClientComponent;
import com.gempukku.secsy.entity.Component;

@ClientComponent
public interface BloomComponent extends Component {
    float getBlurRadius();

    void setBlurRadius(float blurRadius);

    float getMinimalBrightness();

    void setMinimalBrightness(float minimalBrightness);

    float getBloomStrength();

    void setBloomStrength(float bloomStrength);
}
