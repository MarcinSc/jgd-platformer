package jgd.platformer.gameplay.logic.controls.keyboard;

import com.badlogic.gdx.Input;
import com.gempukku.gaming.rendering.input.KeyDown;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.controls.PlayerActivated;

@RegisterSystem(
        profiles = {"gameplay", "keyboard"}
)
public class KeyboardActivateSystem {
    private int[] activateKeys = {Input.Keys.SHIFT_RIGHT, Input.Keys.E};

    @ReceiveEvent
    public void activateKeyPressed(KeyDown keyDown, EntityRef entityRef) {
        if (isKey(activateKeys, keyDown.getKeyCode())) {
            entityRef.send(new PlayerActivated());
        }
    }

    private boolean isKey(int[] keys, int keyCode) {
        for (int key : keys) {
            if (key == keyCode)
                return true;
        }

        return false;
    }
}
