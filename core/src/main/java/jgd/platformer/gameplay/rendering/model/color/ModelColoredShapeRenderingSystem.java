package jgd.platformer.gameplay.rendering.model.color;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.rendering.shape.ShapeProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import jgd.platformer.gameplay.rendering.model.GetModelInstance;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameScreen"
)
public class ModelColoredShapeRenderingSystem implements LifeCycleSystem {
    private static final String MODELS_ATLAS_ID = "models";

    @Inject
    private EntityManager entityManager;
    @Inject
    private ShapeProvider shapeProvider;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private Map<EntityRef, ModelInstance> modelInstances = new HashMap<>();
    private Model model;

    @Override
    public void destroy() {
        model.dispose();
    }

    @Override
    public void initialize() {
        Material material = new Material();

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(1, 1, 1, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked);
    }

    @ReceiveEvent
    public void objectAdded(AfterComponentAdded event, EntityRef entity, ModelColoredCuboidComponent modelColoredShape) {
        modelInstances.put(entity, new ModelInstance(model));
    }

    @ReceiveEvent
    public void objectRemoved(BeforeComponentRemoved event, EntityRef entity, ModelColoredCuboidComponent modelRender) {
        modelInstances.remove(entity);
    }

    @ReceiveEvent
    public void returnShapeModelInstance(GetModelInstance event, EntityRef entityRef, ModelColoredCuboidComponent modelColoredCuboid) {
        ModelInstance result = modelInstances.get(entityRef);

        for (Material material : result.materials) {
            material.set(ColorAttribute.createDiffuse(modelColoredCuboid.getColor()));
        }

        result.transform.idt().translate(event.getLocation());
        result.transform.rotate(0, 1, 0, event.getRotationY());

        Vector3 scale = modelColoredCuboid.getScale();
        result.transform.translate(modelColoredCuboid.getTranslate().add(scale.scl(0.5f)));
        scale.scl(2f);

        result.transform.scale(
                event.getScale().x * scale.x,
                event.getScale().y * scale.y,
                event.getScale().z * scale.z);

        event.appendModelInstance(result);
    }
}
