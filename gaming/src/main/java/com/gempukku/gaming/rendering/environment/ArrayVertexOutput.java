package com.gempukku.gaming.rendering.environment;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class ArrayVertexOutput implements VertexOutput {
    private FloatArray vertices;
    private ShortArray indices;

    private short vertexIndex;
    private float x;
    private float y;
    private float z;
    private float normalX;
    private float normalY;
    private float normalZ;
    private float textureCoordX;
    private float textureCoordY;

    public ArrayVertexOutput() {
        this.vertices = new FloatArray();
        this.indices = new ShortArray();
    }

    @Override
    public short finishVertex() {
        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
        vertices.add(normalX);
        vertices.add(normalY);
        vertices.add(normalZ);
        vertices.add(textureCoordX);
        vertices.add(textureCoordY);

        x = y = z = normalX = normalY = normalZ = textureCoordX = textureCoordY = 0;

        return vertexIndex++;
    }

    @Override
    public void setNormal(float x, float y, float z) {
        normalX = x;
        normalY = y;
        normalZ = z;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void setTextureCoordinate(float x, float y) {
        textureCoordX = x;
        textureCoordY = y;
    }

    @Override
    public void addVertexIndex(short vertexIndex) {
        indices.add(vertexIndex);
    }

    @Override
    public void clear() {
        vertices.clear();
        indices.clear();
    }

    @Override
    public MeshPart generateMeshPart(String partId) {
        if (indices.size == 0)
            return null;

        Mesh mesh = new Mesh(true, vertices.size / 8, indices.size,
                VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
        mesh.setVertices(vertices.toArray());
        mesh.setIndices(indices.toArray());

        return new MeshPart(partId, mesh, 0, indices.size, GL20.GL_TRIANGLES);
    }
}
