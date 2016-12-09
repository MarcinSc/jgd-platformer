package jgd.platformer.gameplay.rendering.platform;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderProvider;
import com.gempukku.gaming.gdx.pluggable.PluggableShaderUtil;
import com.gempukku.gaming.rendering.environment.ArrayVertexOutput;
import com.gempukku.gaming.rendering.event.RenderEnvironment;
import com.gempukku.gaming.rendering.shape.ShapeOutput;
import com.gempukku.gaming.rendering.shape.ShapeProvider;
import com.gempukku.gaming.rendering.shape.TextureRegionMapper;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.level.AfterLevelLoaded;
import jgd.platformer.gameplay.level.BeforeLevelUnloaded;
import jgd.platformer.gameplay.level.BlockComponent;
import jgd.platformer.gameplay.level.LevelComponent;

import java.util.Map;

@RegisterSystem(profiles = "gameScreen")
public class BlockRenderer implements LifeCycleSystem {
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
    private ModelInstance blocks;

    @Override
    public void preInitialize() {
        modelBatch = new ModelBatch(new PluggableShaderProvider(PluggableShaderUtil.createDefaultPluggableShaderBuilder()));
    }

    @Override
    public void postDestroy() {
        destroyMesh();
    }

    @ReceiveEvent
    public void levelLoader(AfterLevelLoaded event, EntityRef entity, LevelComponent level) {
        createMesh(level);
    }

    private void createMesh(LevelComponent level) {
        ArrayVertexOutput vertices = new ArrayVertexOutput();

        Map<String, String> blockCoordinates = level.getBlockCoordinates();
        if (!blockCoordinates.isEmpty()) {
            for (Map.Entry<String, String> locationToBlock : blockCoordinates.entrySet()) {
                String locationAsString = locationToBlock.getKey();
                String[] locationSplit = locationAsString.split(",");
                float x = Float.parseFloat(locationSplit[0]);
                float y = Float.parseFloat(locationSplit[1]);
                float z = Float.parseFloat(locationSplit[2]);

                String blockPrefabName = locationToBlock.getValue();
                EntityData blockData = prefabManager.getPrefabByName(blockPrefabName);
                EntityRef blockEntity = entityManager.wrapEntityData(blockData);
                BlockComponent blockDef = blockEntity.getComponent(BlockComponent.class);
                Vector3 translate = blockDef.getTranslate();

                ShapeOutput.outputShapeToVertexOutput(vertices, shapeProvider.getShapeById(blockDef.getShape()), new TextureRegionMapper() {
                    @Override
                    public TextureRegion getTextureRegion(String textureId) {
                        String textureName = blockDef.getTexturesForParts().get(textureId);
                        if (textureName == null)
                            return null;
                        return textureAtlasProvider.getTexture("platforms", textureName);
                    }
                }, x + translate.x, y + translate.y, z + translate.z);
            }

            MeshPart platform = vertices.generateMeshPart("platform");

            Material material = new Material(TextureAttribute.createDiffuse(textureAtlasProvider.getTextures("platforms").get(0)));

            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            modelBuilder.part(platform, material);

            model = modelBuilder.end();

            blocks = new ModelInstance(model);
        }
    }

    @ReceiveEvent
    public void levelUnloaded(BeforeLevelUnloaded event, EntityRef entity, LevelComponent level) {
        destroyMesh();
    }

    @ReceiveEvent
    public void rebuildBlockMesh(RebuildBlockMesh event, EntityRef entity, LevelComponent level) {
        destroyMesh();
        createMesh(level);
    }

    private void destroyMesh() {
        if (model != null) {
            model.dispose();
            model = null;
        }
        blocks = null;
    }

    @ReceiveEvent
    public void renderBlocks(RenderEnvironment event, EntityRef renderingEntity) {
        if (blocks != null) {
            event.getRenderPipeline().getCurrentBuffer().begin();
            modelBatch.begin(event.getCamera());
            modelBatch.render(blocks, event.getEnvironment());
            modelBatch.end();
            event.getRenderPipeline().getCurrentBuffer().end();
        }
    }
}
