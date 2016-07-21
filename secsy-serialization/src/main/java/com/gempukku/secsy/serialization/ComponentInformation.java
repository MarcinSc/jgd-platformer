package com.gempukku.secsy.serialization;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.io.ComponentData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ComponentInformation implements ComponentData {
    private Class<? extends Component> clazz;
    private Map<String, Object> fields = new HashMap<>();

    public ComponentInformation(Class<? extends Component> clazz) {
        this.clazz = clazz;
    }

    public ComponentInformation(ComponentData toCopy) {
        this(toCopy.getComponentClass());

        fields.putAll(toCopy.getFields());
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return clazz;
    }

    public void addField(String name, Object value) {
        fields.put(name, value);
    }

    @Override
    public Map<String, Object> getFields() {
        return Collections.unmodifiableMap(fields);
    }
}
