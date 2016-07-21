package jgd.platformer.gameplay.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.gaming.rendering.environment.ArrayVertexOutput;
import com.gempukku.gaming.rendering.event.RenderEnvironment;
import com.gempukku.gaming.rendering.shape.ShapeDef;
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
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.component.ModelRenderComponent;
import jgd.platformer.gameplay.component.ModelShapeComponent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(profiles = "gameplay")
public class ModelRenderer implements LifeCycleSystem {
    private static final String CHARACTERS_ATLAS_ID = "models";
    @Inject
    private ShapeProvider shapeProvider;
    @Inject
    private TextureAtlasRegistry textureAtlasRegistry;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    private ModelBatch modelBatch = new ModelBatch();

    private Map<EntityRef, ModelInstance> modelInstances = new HashMap<>();
    private Map<String, Model> models = new HashMap<>();
    private EntityIndex charactersIndex;

    @Override
    public void initialize() {
        charactersIndex = entityIndexManager.addIndexOnComponents(ModelRenderComponent.class, LocationComponent.class);

        Set<String> textureNames = new HashSet<>();

        for (EntityData entityData : prefabManager.findPrefabsWithComponents(ModelShapeComponent.class)) {
            EntityRef entityRef = entityManager.wrapEntityData(entityData);
            for (String textureName : entityRef.getComponent(ModelShapeComponent.class).getTexturesForParts().values()) {
                textureNames.add(textureName);
            }
        }

        textureAtlasRegistry.registerTextures(CHARACTERS_ATLAS_ID, textureNames);
    }

    @Override
    public void postDestroy() {
        for (Model model : models.values()) {
            model.dispose();
        }
    }

    @ReceiveEvent
    public void characterAdded(AfterComponentAdded event, EntityRef entity, ModelRenderComponent characterRender, LocationComponent location) {
        String modelPrefab = characterRender.getModelPrefab();
        if (!models.containsKey(modelPrefab)) {
            EntityRef modelEntity = entityManager.wrapEntityData(prefabManager.getPrefabByName(modelPrefab));
            ModelShapeComponent modelShape = modelEntity.getComponent(ModelShapeComponent.class);

            ShapeDef characterShape = shapeProvider.getShapeById(modelShape.getShape());

            ArrayVertexOutput arrayVertexOutput = new ArrayVertexOutput();
            ShapeOutput.outputShapeToVertexOutput(arrayVertexOutput, characterShape, new TextureRegionMapper() {
                @Override
                public TextureRegion getTextureRegion(String textureId) {
                    String textureName = modelShape.getTexturesForParts().get(textureId);
                    if (textureName == null)
                        return null;
                    return textureAtlasProvider.getTexture(CHARACTERS_ATLAS_ID, textureName);
                }
            }, 0, 0, 0);

            MeshPart platform = arrayVertexOutput.generateMeshPart("character");

            Material material = new Material(TextureAttribute.createDiffuse(textureAtlasProvider.getTextures(CHARACTERS_ATLAS_ID).get(0)),
                    new BlendingAttribute());

            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            modelBuilder.part(platform, material);

            Model model = modelBuilder.end();
            models.put(modelPrefab, model);
        }
        modelInstances.put(entity, new ModelInstance(models.get(modelPrefab)));
    }

    @ReceiveEvent(priority = -1)
    public void renderCharacters(RenderEnvironment event, EntityRef renderingEntity) {
        if (!modelInstances.isEmpty()) {
            for (EntityRef entityRef : charactersIndex.getEntities()) {
                ModelRenderComponent characterRender = entityRef.getComponent(ModelRenderComponent.class);
                LocationComponent location = entityRef.getComponent(LocationComponent.class);

                ModelInstance modelInstance = modelInstances.get(entityRef);
                modelInstance.transform = new Matrix4().translate(
                        location.getX() + characterRender.getTranslateX(),
                        location.getY() + characterRender.getTranslateY(),
                        location.getZ() + characterRender.getTranslateZ());
            }

            modelBatch.begin(event.getCamera());
            modelBatch.render(modelInstances.values());
            modelBatch.end();
        }
    }

    @ReceiveEvent
    public void characterRemoved(BeforeComponentRemoved event, EntityRef entity, ModelRenderComponent characterRender, LocationComponent location) {
        modelInstances.remove(entity);
    }
}
