package com.gempukku.libgdx.shader.pluggable;

public class PluggableShaderFeatureRegistry {
    private static int nextAvailableBit;

    public static synchronized PluggableShaderFeature registerFeature() {
        return new PluggableShaderFeature(nextAvailableBit++);
    }

    public static class PluggableShaderFeature {
        private int bit;

        private PluggableShaderFeature(int bit) {
            this.bit = bit;
        }

        public int getBit() {
            return bit;
        }
    }
}
