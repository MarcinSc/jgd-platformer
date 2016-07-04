package jgd.platformer.logic.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameLoop;
import com.gempukku.secsy.entity.game.GameLoopListener;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import jgd.platformer.component.LocationComponent;

@RegisterSystem(
        profiles = "keyboard"
)
public class KeyboardSystem implements LifeCycleSystem, GameLoopListener {
    private static final float BLOCKS_PER_MILLISECOND = (4f / 1000);
    @Inject
    private GameLoop gameLoop;
    @Inject
    private TimeManager timeManager;
    @Inject
    private EntityIndexManager entityIndexManager;

    private int[] leftKeys = {Input.Keys.LEFT, Input.Keys.A};
    private int[] rightKeys = {Input.Keys.RIGHT, Input.Keys.D};
    private EntityIndex controlledEntities;

    @Override
    public void initialize() {
        gameLoop.addGameLoopListener(this);

        controlledEntities = entityIndexManager.addIndexOnComponents(PlayerControlledComponent.class, LocationComponent.class);
    }

    @Override
    public void update() {
        boolean leftPressed = isLeftPressed();
        boolean rightPressed = isRightPressed();

        long timeSinceLastUpdate = timeManager.getTimeSinceLastUpdate();

        float xDiff = 0;

        if (leftPressed && !rightPressed) {
            xDiff -= timeSinceLastUpdate * BLOCKS_PER_MILLISECOND;
        } else if (rightPressed && !leftPressed) {
            xDiff += timeSinceLastUpdate * BLOCKS_PER_MILLISECOND;
        }

        if (xDiff != 0) {
            for (EntityRef entityRef : controlledEntities.getEntities()) {
                LocationComponent location = entityRef.getComponent(LocationComponent.class);
                location.setX(location.getX() + xDiff);
                entityRef.saveChanges();
            }
        }
    }

    private boolean isLeftPressed() {
        return isAnyPressed(leftKeys);
    }

    private boolean isRightPressed() {
        return isAnyPressed(rightKeys);
    }

    private boolean isAnyPressed(int[] keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key))
                return true;
        }
        return false;
    }
}
