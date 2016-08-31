package jgd.platformer.gameplay.rendering.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gempukku.gaming.rendering.GetCamera;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.gameplay.component.LocationComponent;
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

        cameraFocusedEntities = entityIndexManager.addIndexOnComponents(CameraFocusComponent.class, LocationComponent.class);
        cameraBoundsEntities = entityIndexManager.addIndexOnComponents(CameraBoundsComponent.class);
    }

    @ReceiveEvent
    public void getCamera(GetCamera getCamera, EntityRef entityRef, FocusBasedCameraComponent focusBasedCamera) {
        camera.viewportWidth = getCamera.getWidth();
        camera.viewportHeight = getCamera.getHeight();

        float weightSum = 0;
        float sumX = 0;
        float sumY = 0;

        float cameraDistanceY = focusBasedCamera.getDistanceY();
        int z = focusBasedCamera.getZ();

        for (EntityRef focusedEntity : cameraFocusedEntities.getEntities()) {
            CameraFocusComponent cameraFocus = focusedEntity.getComponent(CameraFocusComponent.class);
            float weight = cameraFocus.getFocusWeight();
            if (weight > 0) {
                LocationComponent location = focusedEntity.getComponent(LocationComponent.class);
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

        camera.position.set(resultX, resultY + cameraDistanceY, z);

        camera.lookAt(resultX, resultY + cameraDistanceY, 0);
        camera.up.set(0, 1, 0);
        camera.update();

        getCamera.setCamera(camera);
    }
}
