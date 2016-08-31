package com.gempukku.gaming.rendering.backdrop.background.color;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.rendering.event.RenderBackdrop;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

@RegisterSystem
public class BackgroundColorRenderer implements LifeCycleSystem {
    private ModelBatch modelBatch;

    private BackgroundColorShaderProvider backgroundShaderProvider;
    private ModelInstance modelInstance;
    private Model model;

    @Override
    public void preInitialize() {
        backgroundShaderProvider = new BackgroundColorShaderProvider();

        modelBatch = new ModelBatch(backgroundShaderProvider);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder backgroundBuilder = modelBuilder.part("background", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position,
                new Material(new DepthTestAttribute(false)));
        backgroundBuilder.rect(
                -1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                1, 1, 1,
                0, 0, 1);
        model = modelBuilder.end();

        modelInstance = new ModelInstance(model);
    }

    @ReceiveEvent(priorityName = "gaming.renderer.backgroundColor")
    public void renderBackground(RenderBackdrop event, EntityRef renderingEntity, BackgroundColorComponent backgroundColor) {
        Vector3 color = new Vector3(
                backgroundColor.getRed() / 255f,
                backgroundColor.getGreen() / 255f,
                backgroundColor.getBlue() / 255f);
        backgroundShaderProvider.setBackgroundColor(color);

        event.getRenderPipeline().getCurrentBuffer().begin();
        modelBatch.begin(event.getCamera());
        modelBatch.render(modelInstance);
        modelBatch.end();
        event.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void postDestroy() {
        modelBatch.dispose();
        model.dispose();
    }
}
