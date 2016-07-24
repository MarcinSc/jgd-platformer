package com.gempukku.gaming.ai;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface AIComponent extends Component {
    String getAiName();

    Map<String, Object> getValues();
}
