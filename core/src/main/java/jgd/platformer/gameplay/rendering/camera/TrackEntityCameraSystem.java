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
public class TrackEntityCameraSystem implements LifeCycleSystem {
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
    public void getCamera(GetCamera getCamera, EntityRef entityRef, TrackEntityCameraComponent trackEntityCamera, LocationComponent location) {
        camera.viewportWidth = getCamera.getWidth();
        camera.viewportHeight = getCamera.getHeight();

        float cameraDistanceY = trackEntityCamera.getDistanceY();
        int distanceZ = trackEntityCamera.getZ();

        float x = location.getX();
        float y = location.getY() + cameraDistanceY;
        float z = location.getZ();
        camera.position.set(x, y, z + distanceZ);

        camera.lookAt(x, y, z);
        camera.up.set(0, 1, 0);
        camera.update();

        getCamera.setCamera(camera);
    }
}
