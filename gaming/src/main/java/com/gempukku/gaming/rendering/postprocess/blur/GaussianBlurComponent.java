package com.gempukku.gaming.rendering.postprocess.blur;

import com.gempukku.gaming.component.ClientComponent;
import com.gempukku.secsy.entity.Component;

@ClientComponent
public interface GaussianBlurComponent extends Component {
    int getBlurRadius();

    void setBlurRadius(int blurRadius);
}
