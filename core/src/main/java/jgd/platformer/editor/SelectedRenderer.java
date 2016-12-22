package jgd.platformer.editor;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.rendering.event.RenderEnvironment;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderProvider;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderUtil;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.editor.ui.BlockSelected;
import jgd.platformer.editor.ui.EntitySelected;
import jgd.platformer.editor.ui.ObjectInEditorComponent;
import jgd.platformer.editor.ui.SelectionCleared;
import jgd.platformer.gameplay.component.Location3DComponent;

@RegisterSystem(
        profiles = {"gameScreen", "editor"}
)
public class SelectedRenderer implements LifeCycleSystem {
    private EntityRef selectedEntity;
    private String blockSelected;

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
        modelBatch = new ModelBatch(new PluggableShaderProvider(PluggableShaderUtil.createDefaultPluggableShaderBuilder()));

    }

    @ReceiveEvent
    public void selectionCleared(SelectionCleared event, EntityRef entityRef) {
        selectedEntity = null;
        blockSelected = null;
    }

    @ReceiveEvent
    public void entitySelected(EntitySelected entitySelected, EntityRef entityRef) {
        selectedEntity = entityRef;
        blockSelected = null;
    }

    @ReceiveEvent
    public void blockSelected(BlockSelected blockSelected, EntityRef entityRef) {
        this.blockSelected = blockSelected.getLocation();
        selectedEntity = null;
    }


    @ReceiveEvent
    public void renderCube(RenderEnvironment event, EntityRef entityRef) {
        if (selectedEntity != null) {
            ObjectInEditorComponent objectInEditor = selectedEntity.getComponent(ObjectInEditorComponent.class);
            Vector3 location = selectedEntity.getComponent(Location3DComponent.class).getLocation();

            Vector3 placementTranslate = objectInEditor.getPlacementTranslate();
            Vector3 renderTranslate = objectInEditor.getRenderTranslate();
            Vector3 renderSize = objectInEditor.getRenderSize();

            modelInstance.transform.idt().translate(
                    location.x - placementTranslate.x + renderTranslate.x,
                    location.y - placementTranslate.y + renderTranslate.y,
                    location.z - placementTranslate.z + renderTranslate.z
            ).scl(renderSize);

            event.getRenderPipeline().getCurrentBuffer().begin();
            modelBatch.begin(event.getCamera());
            modelBatch.render(modelInstance);
            modelBatch.end();
            event.getRenderPipeline().getCurrentBuffer().end();
        }
        if (blockSelected != null) {
            String[] locationSplit = blockSelected.split(",");
            float x = Float.parseFloat(locationSplit[0]);
            float y = Float.parseFloat(locationSplit[1]);
            float z = Float.parseFloat(locationSplit[2]);

            modelInstance.transform.idt().translate(
                    x, y, z);

            event.getRenderPipeline().getCurrentBuffer().begin();
            modelBatch.begin(event.getCamera());
            modelBatch.render(modelInstance);
            modelBatch.end();
            event.getRenderPipeline().getCurrentBuffer().end();
        }
    }
}
