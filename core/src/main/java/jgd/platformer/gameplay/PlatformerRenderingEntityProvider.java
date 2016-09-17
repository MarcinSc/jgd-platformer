package jgd.platformer.gameplay;

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
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.rendering.light.PointLightComponent;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"},
        shared = RenderingEntityProvider.class
)
public class PlatformerRenderingEntityProvider implements RenderingEntityProvider, LifeCycleSystem {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    private EntityRef cameraEntity;

    private Environment environment = new Environment();
    private PointLight cameraLight = new PointLight();
    private Map<EntityRef, PointLight> pointLights = new HashMap<>();

    @Override
    public void initialize() {
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
}
