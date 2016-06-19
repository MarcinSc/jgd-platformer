package com.gempukku.gaming.rendering.environment;

import com.badlogic.gdx.graphics.g3d.model.MeshPart;

public interface VertexOutput {
    void setPosition(float x, float y, float z);

    void setNormal(float x, float y, float z);

    void setTextureCoordinate(float x, float y);

    short finishVertex();

    void addVertexIndex(short vertexIndex);

    MeshPart generateMeshPart(String partId);

    void clear();
}
