package jgd.platformer.common.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gempukku.gaming.rendering.GetCamera;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "parent")
public class DefinedCameraSystem {
    private Camera camera = new PerspectiveCamera(75, 0, 0);

    @ReceiveEvent
    public void getCamera(GetCamera getCamera, EntityRef entityRef, DefinedCameraComponent definedCamera) {
        camera.viewportWidth = getCamera.getWidth();
        camera.viewportHeight = getCamera.getHeight();
        camera.position.set(definedCamera.getLocation());
        camera.lookAt(definedCamera.getLookAt());
        camera.up.set(definedCamera.getUp());
        camera.update();

        getCamera.setCamera(camera);
    }
}
