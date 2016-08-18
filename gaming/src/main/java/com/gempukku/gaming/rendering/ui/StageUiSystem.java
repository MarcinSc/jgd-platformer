package com.gempukku.gaming.rendering.ui;

import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gempukku.gaming.rendering.ScreenResized;
import com.gempukku.gaming.rendering.event.UiRendering;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "stageUi",
        shared = {UiProcessor.class, StageProvider.class}
)
public class StageUiSystem implements UiProcessor, StageProvider {
    @Inject
    private TimeManager timeManager;

    private Stage stage = new Stage(new ScreenViewport());

    @ReceiveEvent(priorityName = "gaming.renderer.stage")
    public void renderUi(UiRendering event, EntityRef renderingEntity) {
        event.getRenderPipeline().getCurrentBuffer().begin();
        stage.draw();
        event.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void processUi(InputEventQueue inputEventQueue) {
        inputEventQueue.setProcessor(this);
        inputEventQueue.drain();
        processUi();
    }

    @Override
    public void processUi() {
        stage.act(timeManager.getTimeSinceLastUpdate() / 1000f);
    }

    @ReceiveEvent
    public void screenResized(ScreenResized screenResized, EntityRef entity) {
        stage.getViewport().update(screenResized.getWidth(), screenResized.getHeight(), true);
        stage.getViewport().apply();
    }

    @Override
    public boolean keyDown(int keycode) {
        return stage.keyDown(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return stage.keyTyped(character);
    }

    @Override
    public boolean keyUp(int keyCode) {
        return stage.keyUp(keyCode);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return stage.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return stage.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return stage.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return stage.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean scrolled(int amount) {
        return stage.scrolled(amount);
    }
}
