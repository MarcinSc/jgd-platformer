package com.gempukku.gaming.rendering.shape;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.gaming.rendering.environment.TextureRegionMapper;
import com.gempukku.gaming.rendering.environment.VertexOutput;

import java.util.List;

public class ShapeOutput {
    private ShapeOutput() {

    }

    public static void outputShapeToVertexOutput(VertexOutput vertexOutput, ShapeDef shape, TextureRegionMapper textureRegionMapper,
                                                 float xTranslate, float yTranslate, float zTranslate,
                                                 float xMultiply, float yMultiply, float zMultiply) {
        for (ShapePartDef shapePart : shape.getShapeParts()) {
            List<String> textureIds = shapePart.getTextures();

            TextureRegion textureRegion = findFirstTexture(textureRegionMapper, textureIds);
            if (textureRegion != null) {
                int vertexCount = shapePart.getVertices().size();

                // This array will store indexes of vertices in the resulting Mesh
                short[] vertexMapping = new short[vertexCount];
                for (int i = 0; i < vertexCount; i++) {
                    Float[] vertexCoords = shapePart.getVertices().get(i);
                    Float[] normalValues = shapePart.getNormals().get(i);
                    Float[] textureCoords = shapePart.getUvs().get(i);

                    vertexOutput.setPosition(
                            xTranslate + vertexCoords[0] * xMultiply,
                            yTranslate + vertexCoords[1] * yMultiply,
                            zTranslate + vertexCoords[2] * zMultiply);
                    vertexOutput.setNormal(
                            normalValues[0],
                            normalValues[1],
                            normalValues[2]);
                    vertexOutput.setTextureCoordinate(
                            textureRegion.getU() + textureCoords[0] * (textureRegion.getU2() - textureRegion.getU()),
                            textureRegion.getV() + textureCoords[1] * (textureRegion.getV2() - textureRegion.getV()));

                    vertexMapping[i] = vertexOutput.finishVertex();

                }
                for (short index : shapePart.getIndices()) {
                    vertexOutput.addVertexIndex(vertexMapping[index]);
                }
            }
        }
    }

    private static TextureRegion findFirstTexture(TextureRegionMapper textureRegionMapper, List<String> textureIds) {
        for (String textureId : textureIds) {
            TextureRegion result = textureRegionMapper.getTextureRegion(textureId);
            if (result != null)
                return result;
        }
        return null;
    }


}
