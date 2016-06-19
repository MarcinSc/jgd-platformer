package com.gempukku.gaming.rendering.shape;

import java.util.List;

public class ShapeDef {
    private List<ShapePartDef> shapeParts;
    private List<BlockSide> fullParts;

    public List<ShapePartDef> getShapeParts() {
        return shapeParts;
    }

    public void setShapeParts(List<ShapePartDef> shapeParts) {
        this.shapeParts = shapeParts;
    }

    public List<BlockSide> getFullParts() {
        return fullParts;
    }

    public void setFullParts(List<BlockSide> fullParts) {
        this.fullParts = fullParts;
    }
}
