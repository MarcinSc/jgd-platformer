package com.gempukku.gaming.rendering.postprocess.blur;

import com.gempukku.gaming.component.ClientComponent;
import com.gempukku.secsy.entity.Component;

@ClientComponent
public interface BlurComponent extends Component {
    float getBlurRadius();

    void setBlurRadius(float blurRadius);
}
