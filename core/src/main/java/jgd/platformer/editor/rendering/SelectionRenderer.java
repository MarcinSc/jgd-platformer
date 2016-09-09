package jgd.platformer.editor.rendering;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.rendering.event.RenderEnvironment;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.editor.controls.MouseTracked;

@RegisterSystem(
        profiles = {"gameScreen", "editor"})
public class SelectionRenderer implements LifeCycleSystem {
    private Vector3 mousePosition = new Vector3();

    private Model model;
    private ModelInstance modelInstance;

    private ModelBatch modelBatch;

    @Override
    public void initialize() {
        float size = 1;

        float[] vertices = {
                0, 0, 0,
                0, 0, size,
                0, size, 0,
                0, size, size,
                size, 0, 0,
                size, 0, size,
                size, size, 0,
                size, size, size};

        short[] indices = {
                0, 1,
                0, 2,
                1, 3,
                2, 3,

                4, 5,
                4, 6,
                5, 7,
                6, 7,

                0, 4,
                1, 5,
                2, 6,
                3, 7};

        Mesh mesh = new Mesh(true, vertices.length / 3, indices.length, VertexAttribute.Position());
        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("cube", mesh, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(1, 1, 1, 1)));
        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
        modelBatch = new ModelBatch();
    }

    @ReceiveEvent
    public void mouseTracked(MouseTracked mouseTracked, EntityRef entityRef) {
        Vector3 position = mouseTracked.getPosition();
        boolean snap = mouseTracked.isSnap();
        if (snap)
            mousePosition.set(MathUtils.round(position.x - 0.5f), MathUtils.round(position.y - 0.5f), MathUtils.round(position.z));
        else
            mousePosition.set(position.x - 0.5f, position.y - 0.5f, position.z);
    }

    @ReceiveEvent
    public void renderCube(RenderEnvironment event, EntityRef entityRef) {
        if (mousePosition != null) {
            modelInstance.transform.idt().translate(mousePosition);

            event.getRenderPipeline().getCurrentBuffer().begin();
            modelBatch.begin(event.getCamera());
            modelBatch.render(modelInstance);
            modelBatch.end();
            event.getRenderPipeline().getCurrentBuffer().end();
        }
    }
}
