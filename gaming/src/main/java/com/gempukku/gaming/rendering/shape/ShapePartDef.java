package com.gempukku.gaming.rendering.shape;

import java.util.List;

public class ShapePartDef {
    private BlockSide side;
    private List<String> textures;
    private List<Float[]> vertices;
    private List<Short> indices;
    private List<Float[]> normals;
    private List<Float[]> uvs;

    public List<Short> getIndices() {
        return indices;
    }

    public void setIndices(List<Short> indices) {
        this.indices = indices;
    }

    public List<Float[]> getNormals() {
        return normals;
    }

    public void setNormals(List<Float[]> normals) {
        this.normals = normals;
    }

    public BlockSide getSide() {
        return side;
    }

    public void setSide(BlockSide side) {
        this.side = side;
    }

    public List<String> getTextures() {
        return textures;
    }

    public void setTextures(List<String> textures) {
        this.textures = textures;
    }

    public List<Float[]> getUvs() {
        return uvs;
    }

    public void setUvs(List<Float[]> uvs) {
        this.uvs = uvs;
    }

    public List<Float[]> getVertices() {
        return vertices;
    }

    public void setVertices(List<Float[]> vertices) {
        this.vertices = vertices;
    }
}
