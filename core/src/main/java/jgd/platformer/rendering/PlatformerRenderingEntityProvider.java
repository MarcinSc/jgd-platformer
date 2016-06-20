package jgd.platformer.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;

@RegisterSystem(
        shared = RenderingEntityProvider.class
)
public class PlatformerRenderingEntityProvider implements RenderingEntityProvider, LifeCycleSystem {
    @Inject
    private EntityManager entityManager;

    private EntityRef cameraEntity;

    @Override
    public void initialize() {
        cameraEntity = entityManager.createEntity();
    }

    @Override
    public EntityRef getRenderingEntity() {
        return cameraEntity;
    }

    @Override
    public void setupRenderingCamera(Camera camera) {
        camera.position.set(0, 0, 5);
        camera.lookAt(0, 0, 0);
        camera.up.set(0, 1, 0);
    }
}
