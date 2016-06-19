package com.gempukku.gaming.asset.component;

import com.gempukku.secsy.entity.Component;

public interface NameComponentManager {
    Class<? extends Component> getComponentByName(String name);

    String getNameByComponent(Class<? extends Component> componentClass);
}
