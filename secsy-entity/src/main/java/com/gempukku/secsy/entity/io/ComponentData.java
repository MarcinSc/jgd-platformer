package com.gempukku.secsy.entity.io;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface ComponentData {
    Class<? extends Component> getComponentClass();

    Map<String, Object> getFields();
}
