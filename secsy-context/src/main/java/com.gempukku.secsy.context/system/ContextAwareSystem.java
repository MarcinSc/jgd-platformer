package com.gempukku.secsy.context.system;

import com.gempukku.secsy.context.SystemContext;

public interface ContextAwareSystem<S> {
    void setContext(SystemContext<S> context);
}
