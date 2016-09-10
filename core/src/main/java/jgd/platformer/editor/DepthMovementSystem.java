package jgd.platformer.editor;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.common.camera.DefinedCameraComponent;
import jgd.platformer.editor.controls.MoveDepth;

@RegisterSystem(
        profiles = {"gameScreen", "editor"}
)
public class DepthMovementSystem {
    @ReceiveEvent
    public void moveDepth(MoveDepth moveDepth, EntityRef entityRef, DefinedCameraComponent definedCamera) {
        Vector3 location = definedCamera.getLocation();
        Vector3 lookAt = definedCamera.getLookAt();

        int depth = moveDepth.getDepth();
        location.z += depth;
        lookAt.z += depth;

        definedCamera.setLocation(location);
        definedCamera.setLookAt(lookAt);
        entityRef.saveChanges();
    }
}
