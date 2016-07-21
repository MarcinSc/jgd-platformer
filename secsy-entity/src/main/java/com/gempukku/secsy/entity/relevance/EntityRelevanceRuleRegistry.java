package com.gempukku.secsy.entity.relevance;

import com.gempukku.secsy.context.annotation.API;

@API
public interface EntityRelevanceRuleRegistry {
    void registerEntityRelevanceRule(EntityRelevanceRule entityRelevanceRule);

    void deregisterEntityRelevanceRule(EntityRelevanceRule entityRelevanceRule);
}
