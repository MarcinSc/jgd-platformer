package jgd.platformer.editor.rendering;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.rendering.event.RenderEnvironment;
import com.gempukku.gaming.rendering.shape.ShapeProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.rendering.model.GetModelInstance;
import jgd.platformer.gameplay.rendering.model.ModelRotateComponent;
import jgd.platformer.gameplay.rendering.model.ModelScaleComponent;

import java.util.LinkedList;
import java.util.List;

@RegisterSystem(profiles = "gameScreen")
public class ModelEditorRenderer implements LifeCycleSystem {
    @Inject
    private ShapeProvider shapeProvider;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    private ModelBatch modelBatch = new ModelBatch();

    private EntityIndex modelsIndex;

    @Override
    public void initialize() {
        modelsIndex = entityIndexManager.addIndexOnComponents(ModelRenderInEditorComponent.class, Location3DComponent.class);
    }

    @ReceiveEvent(priority = -1)
    public void renderModels(RenderEnvironment event, EntityRef renderingEntity, RenderingEditorComponent renderingEditor) {
        List<ModelInstance> models = new LinkedList<>();
        for (EntityRef entityRef : modelsIndex.getEntities()) {
            Vector3 locationVec = entityRef.getComponent(Location3DComponent.class).getLocation();
            float rotationY = 0;
            ModelRotateComponent rotation = entityRef.getComponent(ModelRotateComponent.class);
            if (rotation != null) {
                rotationY = rotation.getRotateY();
            }
            Vector3 scaleVec = new Vector3(1, 1, 1);
            ModelScaleComponent scale = entityRef.getComponent(ModelScaleComponent.class);
            if (scale != null) {
                scaleVec.set(scale.getScale());
            }

            GetModelInstance getModelInstance = new GetModelInstance(locationVec, rotationY, scaleVec);
            entityRef.send(getModelInstance);

            models.addAll(getModelInstance.getInstances());
        }

        event.getRenderPipeline().getCurrentBuffer().begin();
        modelBatch.begin(event.getCamera());
        modelBatch.render(models, event.getEnvironment());
        modelBatch.end();
        event.getRenderPipeline().getCurrentBuffer().end();
    }
}
