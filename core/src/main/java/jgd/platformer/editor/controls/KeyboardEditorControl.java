package jgd.platformer.editor.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.gaming.rendering.input.KeyDown;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.common.camera.DefinedCameraComponent;

@RegisterSystem(
        profiles = {"gameScreen", "editor", "keyboard"})
public class KeyboardEditorControl {
    @Inject
    private RenderingEntityProvider renderingEntityProvider;

    private int[] leftKeys = {Input.Keys.LEFT, Input.Keys.A};
    private int[] rightKeys = {Input.Keys.RIGHT, Input.Keys.D};
    private int[] upKeys = {Input.Keys.UP, Input.Keys.W};
    private int[] downKeys = {Input.Keys.DOWN, Input.Keys.S};

    @ReceiveEvent
    public void keyPressed(KeyDown keyDown, EntityRef entityRef) {
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();
        DefinedCameraComponent definedCamera = renderingEntity.getComponent(DefinedCameraComponent.class);

        int xChange = 0;
        int yChange = 0;

        int keyCode = keyDown.getKeyCode();
        if (isAnyPressed(leftKeys, keyCode)) {
            xChange = -1;
        } else if (isAnyPressed(rightKeys, keyCode)) {
            xChange = 1;
        } else if (isAnyPressed(upKeys, keyCode)) {
            yChange = 1;
        } else if (isAnyPressed(downKeys, keyCode)) {
            yChange = -1;
        }

        if (xChange != 0 || yChange != 0) {
            Vector3 location = definedCamera.getLocation();
            Vector3 lookAt = definedCamera.getLookAt();
            location.x += xChange;
            location.y += yChange;
            lookAt.x += xChange;
            lookAt.y += yChange;

            definedCamera.setLocation(location);
            definedCamera.setLookAt(lookAt);
            renderingEntity.saveChanges();
        }
    }

    private boolean isAnyPressed(int[] keys, int keyCode) {
        for (int key : keys) {
            if (key == keyCode)
                return true;
        }
        return false;
    }
}
