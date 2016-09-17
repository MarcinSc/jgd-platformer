package com.gempukku.gaming.rendering.backdrop.background.color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
        Color color = backgroundColor.getColor();
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        event.getRenderPipeline().getCurrentBuffer().end();
    }
}
