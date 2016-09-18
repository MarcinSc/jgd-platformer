package jgd.platformer.gameplay.rendering.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.rendering.GetCamera;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.Location3DComponent;
import jgd.platformer.gameplay.rendering.CameraBoundsComponent;
import jgd.platformer.gameplay.rendering.CameraFocusComponent;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class FocusBasedCameraSystem implements LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex cameraFocusedEntities;
    private EntityIndex cameraBoundsEntities;

    private Camera camera;

    @Override
    public void initialize() {
        camera = new PerspectiveCamera(75, 0, 0);

        cameraFocusedEntities = entityIndexManager.addIndexOnComponents(CameraFocusComponent.class, Location3DComponent.class);
        cameraBoundsEntities = entityIndexManager.addIndexOnComponents(CameraBoundsComponent.class);
    }

    @ReceiveEvent
    public void getCamera(GetCamera getCamera, EntityRef entityRef, FocusBasedCameraComponent focusBasedCamera) {
        camera.viewportWidth = getCamera.getWidth();
        camera.viewportHeight = getCamera.getHeight();

        float weightSum = 0;
        float sumX = 0;
        float sumY = 0;
        float sumZ = 0;

        float cameraDistanceY = focusBasedCamera.getDistanceY();
        int z = focusBasedCamera.getZ();

        for (EntityRef focusedEntity : cameraFocusedEntities.getEntities()) {
            CameraFocusComponent cameraFocus = focusedEntity.getComponent(CameraFocusComponent.class);
            float weight = cameraFocus.getFocusWeight();
            if (weight > 0) {
                Vector3 location = focusedEntity.getComponent(Location3DComponent.class).getLocation();
                sumX += location.x * weight;
                sumY += location.y * weight;
                sumZ += location.z * weight;
                weightSum += weight;
            }
        }

        float resultX = sumX / weightSum;
        float resultY = sumY / weightSum;
        float resultZ = sumZ / weightSum;

        EntityRef boundsEntity = cameraBoundsEntities.getEntities().iterator().next();
        CameraBoundsComponent cameraBounds = boundsEntity.getComponent(CameraBoundsComponent.class);
        Vector2 min = cameraBounds.getMin();
        Vector2 max = cameraBounds.getMax();

        resultX = Math.max(Math.min(resultX, max.x), min.x);
        resultY = Math.max(Math.min(resultY, max.y), min.y);

        camera.position.set(resultX, resultY + cameraDistanceY, resultZ + z);

        camera.lookAt(resultX, resultY + cameraDistanceY, resultZ);
        camera.up.set(0, 1, 0);
        camera.update();

        getCamera.setCamera(camera);
    }
}
