package jgd.platformer.gameplay;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
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
import jgd.platformer.gameplay.rendering.CameraBoundsComponent;
import jgd.platformer.gameplay.rendering.CameraFocusComponent;
import jgd.platformer.gameplay.rendering.light.PointLightComponent;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameplay",
        shared = RenderingEntityProvider.class
)
public class PlatformerRenderingEntityProvider implements RenderingEntityProvider, LifeCycleSystem {
    private static final int DISTANCE_FROM_TERRAIN = 8;
    private static final float CAMERA_ABOVE_OBJECTS = 2f;
    private static final float CAMERA_LIGHT_Y_DISTANCE = 3f;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityRef cameraEntity;
    private EntityIndex cameraFocusedEntities;
    private EntityIndex cameraBoundsEntities;

    private Environment environment = new Environment();
    private PointLight cameraLight = new PointLight();
    private Map<EntityRef, PointLight> pointLights = new HashMap<>();

    @Override
    public void initialize() {
        cameraFocusedEntities = entityIndexManager.addIndexOnComponents(CameraFocusComponent.class, LocationComponent.class);
        cameraBoundsEntities = entityIndexManager.addIndexOnComponents(CameraBoundsComponent.class);

        environment.add(cameraLight);
    }

    @Override
    public EntityRef getRenderingEntity() {
        if (cameraEntity == null || !cameraEntity.exists()) {
            EntityData renderingEntity = prefabManager.getPrefabByName("renderingEntity");
            cameraEntity = entityManager.createEntity(renderingEntity);
        }
        return cameraEntity;
    }

    @ReceiveEvent
    public void pointLightAdded(AfterComponentAdded event, EntityRef entityRef, PointLightComponent pointLight, LocationComponent location) {
        PointLight light = new PointLight();
        light.set(new Color(pointLight.getRed() / 255f, pointLight.getGreen() / 255f, pointLight.getBlue() / 255f, 1f),
                location.getX() + pointLight.getTranslateX(),
                location.getY() + pointLight.getTranslateY(),
                location.getZ() + pointLight.getTranslateZ(),
                pointLight.getIntensity());

        pointLights.put(entityRef, light);
        environment.add(light);
    }

    @ReceiveEvent
    public void pointLightRemoved(BeforeComponentRemoved event, EntityRef entityRef, PointLightComponent pointLight, LocationComponent location) {
        PointLight light = pointLights.remove(entityRef);
        environment.remove(light);
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

        camera.position.set(resultX, resultY + CAMERA_ABOVE_OBJECTS, DISTANCE_FROM_TERRAIN);
        cameraLight.set(Color.WHITE, resultX, resultY + CAMERA_ABOVE_OBJECTS + CAMERA_LIGHT_Y_DISTANCE, DISTANCE_FROM_TERRAIN, 130f);

        camera.lookAt(resultX, resultY + CAMERA_ABOVE_OBJECTS, 0);
        camera.up.set(0, 1, 0);
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }
}
