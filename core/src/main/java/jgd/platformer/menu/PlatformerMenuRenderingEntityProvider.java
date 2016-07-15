package jgd.platformer.menu;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        profiles = "menu",
        shared = RenderingEntityProvider.class
)
public class PlatformerMenuRenderingEntityProvider implements RenderingEntityProvider, LifeCycleSystem {
    private static final int DISTANCE_FROM_SCREEN = 8;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    private EntityRef cameraEntity;

    @Override
    public void initialize() {
        EntityData renderingEntity = prefabManager.getPrefabByName("menuRenderingEntity");
        cameraEntity = entityManager.createEntity(renderingEntity);
    }

    @Override
    public EntityRef getRenderingEntity() {
        return cameraEntity;
    }

    @Override
    public void setupRenderingCamera(Camera camera) {
        camera.position.set(0, 0, DISTANCE_FROM_SCREEN);
        camera.lookAt(0, 0, 0);
        camera.up.set(0, 1, 0);
    }
}
