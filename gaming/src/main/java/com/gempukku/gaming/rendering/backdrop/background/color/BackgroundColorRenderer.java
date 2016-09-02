package com.gempukku.gaming.rendering.backdrop.background.color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gempukku.gaming.rendering.event.RenderBackdrop;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem(
        profiles = "backgroundColor")
public class BackgroundColorRenderer {
    @ReceiveEvent(priorityName = "gaming.renderer.backgroundColor")
    public void renderBackground(RenderBackdrop event, EntityRef renderingEntity, BackgroundColorComponent backgroundColor) {
        event.getRenderPipeline().getCurrentBuffer().begin();
        Gdx.gl.glClearColor(backgroundColor.getRed() / 255f, backgroundColor.getGreen() / 255f, backgroundColor.getBlue() / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        event.getRenderPipeline().getCurrentBuffer().end();
    }
}
