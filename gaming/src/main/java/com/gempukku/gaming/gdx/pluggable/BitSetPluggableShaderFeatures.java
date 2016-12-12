package com.gempukku.gaming.gdx.pluggable;

import java.util.BitSet;

public class BitSetPluggableShaderFeatures implements PluggableShaderFeatures {
    private BitSet featureSet = new BitSet();

    @Override
    public void addFeature(PluggableShaderFeatureRegistry.PluggableShaderFeature pluggableShaderFeature) {
        featureSet.set(pluggableShaderFeature.getBit());
    }

    public void clear() {
        featureSet.clear();
    }

    public BitSet getFeatureSet() {
        return featureSet;
    }
}
