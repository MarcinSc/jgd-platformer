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
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.gaming.rendering.environment.ArrayVertexOutput;
import com.gempukku.gaming.rendering.environment.EnvironmentRenderer;
import com.gempukku.gaming.rendering.environment.EnvironmentRendererRegistry;
import com.gempukku.gaming.rendering.shape.ShapeOutput;
import com.gempukku.gaming.rendering.shape.ShapeProvider;
import com.gempukku.gaming.rendering.shape.TextureRegionMapper;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.level.BlockComponent;
import jgd.platformer.level.LevelComponent;

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
    @Inject
    private EntityManager entityManager;
    @Inject
    private PrefabManager prefabManager;

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
    }

    @Override
    public void postDestroy() {
        destroyTerrain();
    }

    @Override
    public void renderEnvironmentForLight(Camera lightCamera) {

    }

    @ReceiveEvent
    public void levelLoader(AfterComponentAdded event, EntityRef entity, LevelComponent level) {
        ArrayVertexOutput vertices = new ArrayVertexOutput();

        for (Map.Entry<String, String> locationToBlock : level.getBlockCoordinates().entrySet()) {
            String locationAsString = locationToBlock.getKey();
            String[] locationSplit = locationAsString.split(",");
            float x = Float.parseFloat(locationSplit[0]);
            float y = Float.parseFloat(locationSplit[1]);
            float z = Float.parseFloat(locationSplit[2]);

            String blockPrefabName = locationToBlock.getValue();
            EntityData blockData = prefabManager.getPrefabByName(blockPrefabName);
            EntityRef blockEntity = entityManager.wrapEntityData(blockData);
            BlockComponent blockDef = blockEntity.getComponent(BlockComponent.class);

            ShapeOutput.outputShapeToVertexOutput(vertices, shapeProvider.getShapeById(blockDef.getShape()), new TextureRegionMapper() {
                @Override
                public TextureRegion getTextureRegion(String textureId) {
                    String textureName = blockDef.getTexturesForParts().get(textureId);
                    if (textureName == null)
                        return null;
                    return textureAtlasProvider.getTexture("platforms", textureName);
                }
            }, x, y, z);
        }

        MeshPart platform = vertices.generateMeshPart("platform");

        Material material = new Material(TextureAttribute.createDiffuse(textureAtlasProvider.getTextures("platforms").get(0)));

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part(platform, material);

        model = modelBuilder.end();

        terrain = new ModelInstance(model);
    }

    @ReceiveEvent
    public void levelUnloaded(BeforeComponentRemoved event, EntityRef entity, LevelComponent level) {
        destroyTerrain();
    }

    private void destroyTerrain() {
        if (model != null) {
            model.dispose();
            model = null;
        }
        terrain = null;
    }

    @Override
    public void renderEnvironment(boolean hasDirectionalLight, Camera camera, Camera lightCamera, Texture lightTexture, int shadowFidelity, float ambientLight) {
        if (terrain != null) {
            modelBatch.begin(camera);
            modelBatch.render(terrain);
            modelBatch.end();
        }
    }
}
