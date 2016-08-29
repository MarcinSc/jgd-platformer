package jgd.platformer.gameplay.rendering.model.shape;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.gaming.rendering.environment.ArrayVertexOutput;
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
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.rendering.model.GetModelInstance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(
        profiles = "gameScreen"
)
public class ModelShapeRenderingSystem implements LifeCycleSystem {
    private static final String MODELS_ATLAS_ID = "models";

    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private ShapeProvider shapeProvider;
    @Inject
    private TextureAtlasRegistry textureAtlasRegistry;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private Map<EntityRef, ModelInstance> modelInstances = new HashMap<>();
    private Map<String, Model> models = new HashMap<>();

    @Override
    public void initialize() {
        Set<String> textureNames = new HashSet<>();

        for (EntityData entityData : prefabManager.findPrefabsWithComponents(ModelShapeComponent.class)) {
            EntityRef entityRef = entityManager.wrapEntityData(entityData);
            for (String textureName : entityRef.getComponent(ModelShapeComponent.class).getTexturesForParts().values()) {
                textureNames.add(textureName);
            }
        }

        textureAtlasRegistry.registerTextures(MODELS_ATLAS_ID, textureNames);
    }

    @Override
    public void destroy() {
        for (Model model : models.values()) {
            model.dispose();
        }
    }

    @ReceiveEvent
    public void objectAdded(AfterComponentAdded event, EntityRef entity, RenderedShapeComponent modelRender) {
        String modelPrefab = modelRender.getModelPrefab();
        if (!models.containsKey(modelPrefab)) {
            EntityRef modelEntity = entityManager.wrapEntityData(prefabManager.getPrefabByName(modelPrefab));
            ModelShapeComponent modelShape = modelEntity.getComponent(ModelShapeComponent.class);

            ShapeDef modelShapeDef = shapeProvider.getShapeById(modelShape.getShape());

            ArrayVertexOutput arrayVertexOutput = new ArrayVertexOutput();
            ShapeOutput.outputShapeToVertexOutput(arrayVertexOutput, modelShapeDef, new TextureRegionMapper() {
                @Override
                public TextureRegion getTextureRegion(String textureId) {
                    String textureName = modelShape.getTexturesForParts().get(textureId);
                    if (textureName == null)
                        return null;
                    return textureAtlasProvider.getTexture(MODELS_ATLAS_ID, textureName);
                }
            }, 0, 0, 0);

            MeshPart platform = arrayVertexOutput.generateMeshPart("model");

            Material material = new Material(TextureAttribute.createDiffuse(textureAtlasProvider.getTextures(MODELS_ATLAS_ID).get(0)),
                    new BlendingAttribute());

            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            modelBuilder.part(platform, material);

            Model model = modelBuilder.end();
            models.put(modelPrefab, model);
        }
        modelInstances.put(entity, new ModelInstance(models.get(modelPrefab)));
    }

    @ReceiveEvent
    public void objectRemoved(BeforeComponentRemoved event, EntityRef entity, RenderedShapeComponent modelRender) {
        modelInstances.remove(entity);
    }

    @ReceiveEvent
    public void returnShapeModelInstance(GetModelInstance event, EntityRef entityRef, RenderedShapeComponent renderedShape) {
        ModelInstance result = modelInstances.get(entityRef);

        result.transform.idt().translate(event.getLocation());
        result.transform.rotate(0, 1, 0, event.getRotationY());

        result.transform.translate(
                renderedShape.getTranslateX(),
                renderedShape.getTranslateY(),
                renderedShape.getTranslateZ());

        result.transform.scale(
                event.getScale().x * renderedShape.getScaleX(),
                event.getScale().y * renderedShape.getScaleY(),
                event.getScale().z * renderedShape.getScaleZ());

        event.appendModelInstance(result);
    }
}
