package jgd.platformer.gameplay;

import com.badlogic.gdx.graphics.Camera;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.component.LocationComponent;
import jgd.platformer.gameplay.rendering.CameraBoundsComponent;
import jgd.platformer.gameplay.rendering.CameraFocusComponent;

@RegisterSystem(
        profiles = "gameplay",
        shared = RenderingEntityProvider.class
)
public class PlatformerRenderingEntityProvider implements RenderingEntityProvider, LifeCycleSystem {
    private static final int DISTANCE_FROM_TERRAIN = 8;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityRef cameraEntity;
    private EntityIndex cameraFocusedEntities;
    private EntityIndex cameraBoundsEntities;

    @Override
    public void initialize() {
        cameraFocusedEntities = entityIndexManager.addIndexOnComponents(CameraFocusComponent.class, LocationComponent.class);
        cameraBoundsEntities = entityIndexManager.addIndexOnComponents(CameraBoundsComponent.class);
    }

    @Override
    public EntityRef getRenderingEntity() {
        if (cameraEntity == null || !cameraEntity.exists()) {
            EntityData renderingEntity = prefabManager.getPrefabByName("renderingEntity");
            cameraEntity = entityManager.createEntity(renderingEntity);
        }
        return cameraEntity;
    }

    @Override
    public void setupRenderingCamera(Camera camera) {
        float weightSum = 0;
        float sumX = 0;
        float sumY = 0;

        for (EntityRef entityRef : cameraFocusedEntities.getEntities()) {
            CameraFocusComponent cameraFocus = entityRef.getComponent(CameraFocusComponent.class);
            float weight = cameraFocus.getFocusWeight();
            if (weight > 0) {
                LocationComponent location = entityRef.getComponent(LocationComponent.class);
                sumX += location.getX() * weight;
                sumY += location.getY() * weight;
                weightSum += weight;
            }
        }

        float resultX = sumX / weightSum;
        float resultY = sumY / weightSum;

        EntityRef boundsEntity = cameraBoundsEntities.getEntities().iterator().next();
        CameraBoundsComponent cameraBounds = boundsEntity.getComponent(CameraBoundsComponent.class);

        resultX = Math.max(Math.min(resultX, cameraBounds.getMaxX()), cameraBounds.getMinX());
        resultY = Math.max(Math.min(resultY, cameraBounds.getMaxY()), cameraBounds.getMinY());

        camera.position.set(resultX, resultY, DISTANCE_FROM_TERRAIN);
        camera.lookAt(resultX, resultY, 0);
        camera.up.set(0, 1, 0);
    }
}