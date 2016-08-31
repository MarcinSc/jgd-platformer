package jgd.platformer.menu.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gempukku.gaming.rendering.GetCamera;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "menu"
)
public class FixedCameraSystem {
    private Camera camera = new PerspectiveCamera(75, 0, 0);

    @ReceiveEvent
    public void getCamera(GetCamera getCamera, EntityRef entityRef, FixedCameraComponent fixedCamera) {
        camera.viewportWidth = getCamera.getWidth();
        camera.viewportHeight = getCamera.getHeight();
        camera.position.set(fixedCamera.getX(), fixedCamera.getY(), fixedCamera.getZ());
        camera.lookAt(0, 0, 0);
        camera.up.set(0, 1, 0);
        camera.update();

        getCamera.setCamera(camera);
    }
}
