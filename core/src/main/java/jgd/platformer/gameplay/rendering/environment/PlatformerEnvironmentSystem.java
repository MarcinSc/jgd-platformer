package jgd.platformer.gameplay.rendering.environment;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.gempukku.gaming.rendering.GetEnvironment;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.rendering.light.PointLightComponent;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class PlatformerEnvironmentSystem implements LifeCycleSystem {
    private static final float CAMERA_LIGHT_Y_DISTANCE = 3f;

    private Environment environment = new Environment();
    private PointLight cameraLight = new PointLight();
    private Map<EntityRef, PointLight> pointLights = new HashMap<>();

    @Override
    public void initialize() {
        environment.add(cameraLight);
    }

    @ReceiveEvent
    public void pointLightAdded(AfterComponentAdded event, EntityRef entityRef, PointLightComponent pointLight, Location3DComponent location) {
        PointLight light = new PointLight();
        light.set(pointLight.getColor(),
                location.getLocation().add(pointLight.getTranslate()),
                pointLight.getIntensity());

        pointLights.put(entityRef, light);
        environment.add(light);
    }

    @ReceiveEvent
    public void pointLightRemoved(BeforeComponentRemoved event, EntityRef entityRef, PointLightComponent pointLight, Location3DComponent location) {
        PointLight light = pointLights.remove(entityRef);
        environment.remove(light);
    }

    @ReceiveEvent
    public void getEnvironment(GetEnvironment getEnvironment, EntityRef entityRef, PlatformerEnvironmentComponent platformerEnvironmentComponent) {
        Camera camera = getEnvironment.getCamera();

        cameraLight.set(Color.WHITE, camera.position.x, camera.position.y + CAMERA_LIGHT_Y_DISTANCE, camera.position.z, 60f);
        getEnvironment.setEnvironment(environment);
    }
}
