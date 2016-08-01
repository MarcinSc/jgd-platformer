package com.gempukku.gaming.spawn;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.io.ComponentData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModifiedComponentData implements ComponentData {
    private Class<? extends Component> clazz;
    private Map<String, Object> fields = new HashMap<>();

    public ModifiedComponentData(ComponentData baseComponentData) {
        clazz = baseComponentData.getComponentClass();
        fields.putAll(baseComponentData.getFields());
    }

    public boolean removeField(String fieldName) {
        return fields.remove(fieldName) != null;
    }

    public void addField(String fieldName, Object value) {
        fields.put(fieldName, value);
    }

    public boolean containsField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return clazz;
    }

    @Override
    public Map<String, Object> getFields() {
        return Collections.unmodifiableMap(fields);
    }
}
