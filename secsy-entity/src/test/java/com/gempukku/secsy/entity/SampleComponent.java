package com.gempukku.secsy.entity;

import com.gempukku.secsy.entity.component.map.GetProperty;
import com.gempukku.secsy.entity.component.map.SetProperty;

public interface SampleComponent extends Component {
    @GetProperty("value")
    public String getValue();

    @SetProperty("value")
    public void setValue(String value);

    public void undefinedMethod();
}