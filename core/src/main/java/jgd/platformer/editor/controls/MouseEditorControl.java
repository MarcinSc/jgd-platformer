package jgd.platformer.editor.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gempukku.gaming.rendering.GetCamera;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.gaming.rendering.input.MouseMoved;
import com.gempukku.gaming.rendering.input.MouseScrolled;
import com.gempukku.gaming.rendering.input.TouchDown;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.level.AfterLevelLoaded;
import org.lwjgl.opengl.Display;

@RegisterSystem(
        profiles = {"gameScreen", "editor", "mouse"})
public class MouseEditorControl {
    @Inject
    private RenderingEntityProvider renderingEntityProvider;

    private EntityRef levelEntity;
    private Vector3 planeNormal = new Vector3(0, 0, 1);
    private float zCoordinate = 0f;

    @ReceiveEvent
    public void mouseMoved(MouseMoved mouseMoved, EntityRef entityRef) {
        int screenX = mouseMoved.getScreenX();
        int screenY = mouseMoved.getScreenY();

        GetCamera getCamera = new GetCamera(Display.getWidth(), Display.getHeight());
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();
        renderingEntity.send(getCamera);

        Camera camera = getCamera.getCamera();

        Ray pickRay = camera.getPickRay(screenX, screenY);
        Vector3 result = new Vector3();

        boolean intersects = Intersector.intersectRayPlane(pickRay, new Plane(planeNormal, zCoordinate), result);

        boolean noSnap = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) | Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        if (intersects) {
            levelEntity.send(new MouseTracked(result, !noSnap));
        }
    }

    @ReceiveEvent
    public void mouseScrolled(MouseScrolled mouseScrolled, EntityRef entityRef) {
        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();
        int amount = mouseScrolled.getAmount();
        zCoordinate -= amount;
        renderingEntity.send(new MoveDepth(amount));
    }

    @ReceiveEvent
    public void mousePressed(TouchDown touchDown, EntityRef entityRef) {
        levelEntity.send(new PlaceObject());
    }

    @ReceiveEvent
    public void levelLoaded(AfterLevelLoaded event, EntityRef entityRef) {
        levelEntity = entityRef;
    }
}
