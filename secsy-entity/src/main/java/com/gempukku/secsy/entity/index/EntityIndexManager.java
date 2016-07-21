package com.gempukku.secsy.entity.index;

import com.gempukku.secsy.entity.Component;

public interface EntityIndexManager {
    EntityIndex addIndexOnComponents(Class<? extends Component>... components);
}
