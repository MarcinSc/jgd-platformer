package com.gempukku.gaming.ai;

import com.gempukku.secsy.entity.Component;

import java.util.HashMap;
import java.util.Map;

public interface AIComponent extends Component {
    String getAiName();

    default Map<String, Object> getValues() {
        return new HashMap<>();
    }

    void setValues(Map<String, Object> values);
}
