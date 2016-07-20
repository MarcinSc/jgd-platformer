package com.gempukku.gaming.rendering.ui;

import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gempukku.gaming.rendering.ScreenResized;
import com.gempukku.gaming.rendering.event.RenderUi;
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
    public void renderUi(RenderUi event, EntityRef renderingEntity) {
        stage.draw();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void processUi(InputEventQueue inputEventQueue) {
        inputEventQueue.setProcessor(stage);
        inputEventQueue.drain();
        stage.act(timeManager.getTimeSinceLastUpdate() / 1000f);
    }

    @ReceiveEvent
    public void screenResized(ScreenResized screenResized, EntityRef entity) {
        stage.getViewport().update(screenResized.getWidth(), screenResized.getHeight(), true);
        stage.getViewport().apply();
    }
}
