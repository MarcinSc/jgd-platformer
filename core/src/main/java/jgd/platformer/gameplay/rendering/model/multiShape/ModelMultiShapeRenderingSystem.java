package jgd.platformer.gameplay.rendering.model.multiShape;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
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
import jgd.platformer.gameplay.rendering.model.GetModelInstance;
import jgd.platformer.gameplay.rendering.model.shape.ModelShapeComponent;
import jgd.platformer.gameplay.rendering.model.shape.RenderedShapeComponent;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameScreen"
)
public class ModelMultiShapeRenderingSystem implements LifeCycleSystem {
    private static final String MODELS_ATLAS_ID = "models";

    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private ShapeProvider shapeProvider;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private Map<EntityRef, ModelInstance> modelInstances = new HashMap<>();
    private Map<String, Model> models = new HashMap<>();

    @Override
    public void destroy() {
        for (Model model : models.values()) {
            model.dispose();
        }
    }

    @ReceiveEvent
    public void objectAdded(AfterComponentAdded event, EntityRef entity, RenderedMultiShapeComponent modelRender) {
        String modelPrefab = modelRender.getModelPrefab();
        String key = getModelKey(modelRender, modelPrefab);
        if (!models.containsKey(key)) {
            EntityRef modelEntity = entityManager.wrapEntityData(prefabManager.getPrefabByName(modelPrefab));
            ModelShapeComponent modelShape = modelEntity.getComponent(ModelShapeComponent.class);

            ShapeDef modelShapeDef = shapeProvider.getShapeById(modelShape.getShape());

            ArrayVertexOutput arrayVertexOutput = new ArrayVertexOutput();

            float advanceX = 0;
            float advanceY = 0;
            float advanceZ = 0;

            Vector3 shapeAdvance = modelRender.getShapeAdvance();

            for (int i = 0; i < modelRender.getShapeCount(); i++) {
                ShapeOutput.outputShapeToVertexOutput(arrayVertexOutput, modelShapeDef, new TextureRegionMapper() {
                    @Override
                    public TextureRegion getTextureRegion(String textureId) {
                        String textureName = modelShape.getTexturesForParts().get(textureId);
                        if (textureName == null)
                            return null;
                        return textureAtlasProvider.getTexture(MODELS_ATLAS_ID, textureName);
                    }
                }, advanceX, advanceY, advanceZ);

                advanceX += shapeAdvance.x;
                advanceY += shapeAdvance.y;
                advanceZ += shapeAdvance.z;
            }

            MeshPart platform = arrayVertexOutput.generateMeshPart("model");

            Material material = new Material(TextureAttribute.createDiffuse(textureAtlasProvider.getTextures(MODELS_ATLAS_ID).get(0)),
                    new BlendingAttribute());

            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            modelBuilder.part(platform, material);

            Model model = modelBuilder.end();
            models.put(key, model);
        }
        modelInstances.put(entity, new ModelInstance(models.get(key)));
    }

    private String getModelKey(RenderedMultiShapeComponent modelRender, String modelPrefab) {
        return modelPrefab + ":" + modelRender.getShapeCount() + ":" + modelRender.getShapeAdvance();
    }

    @ReceiveEvent
    public void objectRemoved(BeforeComponentRemoved event, EntityRef entity, RenderedShapeComponent modelRender) {
        modelInstances.remove(entity);
    }

    @ReceiveEvent
    public void returnShapeModelInstance(GetModelInstance event, EntityRef entityRef, RenderedMultiShapeComponent renderedShape) {
        ModelInstance result = modelInstances.get(entityRef);

        result.transform.idt().translate(event.getLocation());
        result.transform.rotate(0, 1, 0, event.getRotationY());

        result.transform.translate(renderedShape.getTranslate());

        result.transform.scale(event.getScale().x, event.getScale().y, event.getScale().z);

        event.appendModelInstance(result);
    }
}
