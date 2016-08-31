package jgd.platformer.editor.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gempukku.gaming.rendering.GetCamera;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = {"gameScreen", "editor"}
)
public class NavigableCameraSystem {
    private Camera camera = new PerspectiveCamera(75, 0, 0);

    @ReceiveEvent
    public void getCamera(GetCamera getCamera, EntityRef entityRef, NavigableCameraComponent navigableCamera) {
        camera.viewportWidth = getCamera.getWidth();
        camera.viewportHeight = getCamera.getHeight();

        float resultX = 0;
        float resultY = 0;
        camera.position.set(resultX, resultY + 2, 8);

        camera.lookAt(resultX, resultY + 2, 0);
        camera.up.set(0, 1, 0);
        camera.update();

        getCamera.setCamera(camera);
    }
}
