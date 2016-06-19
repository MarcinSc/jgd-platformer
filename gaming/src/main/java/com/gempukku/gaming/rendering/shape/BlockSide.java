package com.gempukku.gaming.rendering.shape;

import java.util.HashMap;
import java.util.Map;

public enum BlockSide {
    top(0, 1, 0),
    bottom(0, -1, 0),
    // Towards negative Z
    back(0, 0, -1),
    // Towards positive Z
    front(0, 0, 1),
    // Towards positive X
    left(1, 0, 0),
    // Towards negative X
    right(-1, 0, 0);

    private static Map<BlockSide, BlockSide> opposite = new HashMap<>();

    static {
        opposite.put(top, bottom);
        opposite.put(bottom, top);
        opposite.put(left, right);
        opposite.put(right, left);
        opposite.put(front, back);
        opposite.put(back, front);
    }

    private int normalX;
    private int normalY;
    private int normalZ;

    BlockSide(int normalX, int normalY, int normalZ) {
        this.normalX = normalX;
        this.normalY = normalY;
        this.normalZ = normalZ;
    }

    public int getNormalX() {
        return normalX;
    }

    public int getNormalY() {
        return normalY;
    }

    public int getNormalZ() {
        return normalZ;
    }

    public BlockSide getOpposite() {
        switch (this) {
            case top:
                return bottom;
            case bottom:
                return top;
            case front:
                return back;
            case back:
                return front;
            case left:
                return right;
            case right:
                return left;
        }
        return null;
    }
}
