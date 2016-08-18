package com.gempukku.gaming.rendering.input;

import com.badlogic.gdx.InputProcessor;
import com.gempukku.gaming.rendering.ui.UiProcessor;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;

@RegisterSystem(
        profiles="eventInputProcessor", shared = InputProcessor.class
)
public class EventInputProcessorSystem implements InputProcessor {
    @Inject
    private EntityManager entityManager;
    @Inject(optional = true)
    private UiProcessor uiProcessor;

    private EntityRef eventEntity;

    private EntityRef getEventEntity() {
        if (eventEntity == null || !eventEntity.exists())
            eventEntity = entityManager.createEntity();

        return eventEntity;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (uiProcessor != null) {
            if (uiProcessor.keyDown(keycode))
                return true;
        }
        getEventEntity().send(new KeyDown(keycode));
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (uiProcessor != null) {
            if (uiProcessor.keyUp(keycode))
                return true;
        }
        getEventEntity().send(new KeyUp(keycode));
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        if (uiProcessor != null) {
            if (uiProcessor.keyTyped(character))
                return true;
        }
        getEventEntity().send(new KeyTyped(character));
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (uiProcessor != null) {
            if (uiProcessor.touchDown(screenX, screenY, pointer, button))
                return true;
        }
        getEventEntity().send(new TouchDown(screenX, screenY, pointer, button));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (uiProcessor != null) {
            if (uiProcessor.touchUp(screenX, screenY, pointer, button))
                return true;
        }
        getEventEntity().send(new TouchUp(screenX, screenY, pointer, button));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (uiProcessor != null) {
            if (uiProcessor.touchDragged(screenX, screenY, pointer))
                return true;
        }
        getEventEntity().send(new TouchDragged(screenX, screenY, pointer));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (uiProcessor != null) {
            if (uiProcessor.mouseMoved(screenX, screenY))
                return true;
        }
        getEventEntity().send(new MouseMoved(screenX, screenY));
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        if (uiProcessor != null) {
            if (uiProcessor.scrolled(amount))
                return true;
        }
        getEventEntity().send(new MouseScrolled(amount));
        return true;
    }
}
