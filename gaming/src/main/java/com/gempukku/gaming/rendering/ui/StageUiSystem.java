package com.gempukku.gaming.rendering.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gempukku.gaming.rendering.event.RenderUi;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "stageUi",
        shared = {UiProcessor.class, StageProvider.class}
)
public class StageUiSystem implements UiProcessor, StageProvider, LifeCycleSystem {
    private Stage stage = new Stage();

    @Override
    public void initialize() {
        Gdx.input.setInputProcessor(stage);
    }

    @ReceiveEvent
    public void renderUi(RenderUi event, EntityRef renderingEntity) {
        stage.draw();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void processUi(long timeDiff) {
        stage.act(timeDiff / 1000f);
    }
}
