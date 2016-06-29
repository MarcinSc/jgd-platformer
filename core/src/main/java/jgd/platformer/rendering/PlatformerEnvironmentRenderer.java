package jgd.platformer.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.gaming.rendering.environment.ArrayVertexOutput;
import com.gempukku.gaming.rendering.environment.EnvironmentRenderer;
import com.gempukku.gaming.rendering.environment.EnvironmentRendererRegistry;
import com.gempukku.gaming.rendering.shape.MapTextureRegionMapper;
import com.gempukku.gaming.rendering.shape.ShapeDef;
import com.gempukku.gaming.rendering.shape.ShapeOutput;
import com.gempukku.gaming.rendering.shape.ShapeProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem
public class PlatformerEnvironmentRenderer implements EnvironmentRenderer, LifeCycleSystem {
    @Inject
    private EnvironmentRendererRegistry environmentRendererRegistry;
    @Inject
    private TextureAtlasRegistry textureAtlasRegistry;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;
    @Inject
    private ShapeProvider shapeProvider;

    private ModelBatch modelBatch;

    private Model model;
    private ModelInstance terrain;

    @Override
    public void preInitialize() {
        modelBatch = new ModelBatch();
    }

    @Override
    public void initialize() {
        environmentRendererRegistry.registerEnvironmentRenderer(this);

        textureAtlasRegistry.registerTextures("platforms", Arrays.asList("blockTiles/Dirt.png", "blockTiles/Grass.png", "blockTiles/GrassSide.png"));
    }

    @Override
    public void postDestroy() {
        model.dispose();
    }

    @Override
    public void renderEnvironmentForLight(Camera lightCamera) {

    }

    private void initializeTerrain() {
        TextureRegion dirt = textureAtlasProvider.getTexture("platforms", "blockTiles/Dirt.png");
        TextureRegion grass = textureAtlasProvider.getTexture("platforms", "blockTiles/Grass.png");
        TextureRegion grassSide = textureAtlasProvider.getTexture("platforms", "blockTiles/GrassSide.png");

        Map<String, TextureRegion> textureIdForGrass = new HashMap<>();
        textureIdForGrass.put("top", grass);
        textureIdForGrass.put("bottom", dirt);
        textureIdForGrass.put("sides", grassSide);

        ShapeDef cube = shapeProvider.getShapeById("cube");

        ArrayVertexOutput vertices = new ArrayVertexOutput();
        ShapeOutput.outputShapeToVertexOutput(vertices, cube, new MapTextureRegionMapper(textureIdForGrass), 0, -2, 0);
        ShapeOutput.outputShapeToVertexOutput(vertices, cube, new MapTextureRegionMapper(textureIdForGrass), 1, -2, 0);
        ShapeOutput.outputShapeToVertexOutput(vertices, cube, new MapTextureRegionMapper(textureIdForGrass), 2, -2, 0);

        MeshPart platform = vertices.generateMeshPart("platform");

        Material material = new Material(TextureAttribute.createDiffuse(dirt.getTexture()));

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part(platform, material);

        model = modelBuilder.end();

        terrain = new ModelInstance(model);
    }

    @Override
    public void renderEnvironment(boolean hasDirectionalLight, Camera camera, Camera lightCamera, Texture lightTexture, int shadowFidelity, float ambientLight) {
        if (terrain == null)
            initializeTerrain();
        modelBatch.begin(camera);
        modelBatch.render(terrain);
        modelBatch.end();
    }
}
