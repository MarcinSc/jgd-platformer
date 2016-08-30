package com.gempukku.gaming.asset.prefab;

import com.gempukku.secsy.serialization.EntityInformation;

public class NamedEntityInformation extends EntityInformation implements NamedEntityData {
    private String name;

    public NamedEntityInformation(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return null;
    }
}
