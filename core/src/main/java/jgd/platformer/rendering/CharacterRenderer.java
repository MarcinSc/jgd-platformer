package jgd.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
import jgd.platformer.component.CharacterRenderComponent;
import jgd.platformer.component.LocationComponent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(profiles = "gameplay")
public class CharacterRenderer implements LifeCycleSystem {
    private static final String CHARACTERS_ATLAS_ID = "characters";
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

    private Map<String, ModelInstance> modelInstances = new HashMap<>();
    private Map<String, Model> models = new HashMap<>();
    private EntityIndex charactersIndex;

    @Override
    public void initialize() {
        charactersIndex = entityIndexManager.addIndexOnComponents(CharacterRenderComponent.class, LocationComponent.class);

        Set<String> textureNames = new HashSet<>();

        for (EntityData entityData : prefabManager.findPrefabsWithComponents(CharacterRenderComponent.class)) {
            EntityRef entityRef = entityManager.wrapEntityData(entityData);
            for (String textureName : entityRef.getComponent(CharacterRenderComponent.class).getTexturesForParts().values()) {
                textureNames.add(textureName);
            }
        }

        textureAtlasRegistry.registerTextures(CHARACTERS_ATLAS_ID, textureNames);
    }

    @ReceiveEvent
    public void characterAdded(AfterComponentAdded event, EntityRef entity, CharacterRenderComponent characterRender, LocationComponent location) {
        String characterId = characterRender.getId();

        ShapeDef characterShape = shapeProvider.getShapeById(characterRender.getShape());

        ArrayVertexOutput arrayVertexOutput = new ArrayVertexOutput();
        ShapeOutput.outputShapeToVertexOutput(arrayVertexOutput, characterShape, new TextureRegionMapper() {
            @Override
            public TextureRegion getTextureRegion(String textureId) {
                String textureName = characterRender.getTexturesForParts().get(textureId);
                if (textureName == null)
                    return null;
                return textureAtlasProvider.getTexture(CHARACTERS_ATLAS_ID, textureName);
            }
        }, 0, 0, 0);

        MeshPart platform = arrayVertexOutput.generateMeshPart("character");

        Material material = new Material(TextureAttribute.createDiffuse(textureAtlasProvider.getTextures(CHARACTERS_ATLAS_ID).get(0)));

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part(platform, material);

        Model model = modelBuilder.end();
        models.put(characterId, model);
        modelInstances.put(characterId, new ModelInstance(model));
    }

    @ReceiveEvent
    public void renderCharacters(RenderEnvironment event, EntityRef renderingEntity) {
        if (!modelInstances.isEmpty()) {
            for (EntityRef entityRef : charactersIndex.getEntities()) {
                CharacterRenderComponent characterRender = entityRef.getComponent(CharacterRenderComponent.class);
                LocationComponent location = entityRef.getComponent(LocationComponent.class);

                ModelInstance modelInstance = modelInstances.get(characterRender.getId());
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
    public void characterRemoved(BeforeComponentRemoved event, EntityRef entity, CharacterRenderComponent characterRender, LocationComponent location) {
        String characterId = characterRender.getId();
        modelInstances.remove(characterId);
        models.remove(characterId).dispose();
    }
}
