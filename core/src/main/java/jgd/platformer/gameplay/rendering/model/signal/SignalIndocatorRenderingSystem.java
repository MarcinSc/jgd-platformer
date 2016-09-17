package jgd.platformer.gameplay.rendering.model.signal;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import jgd.platformer.gameplay.logic.signal.SignalConsumerComponent;
import jgd.platformer.gameplay.rendering.model.GetModelInstance;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameScreen"
)
public class SignalIndocatorRenderingSystem implements LifeCycleSystem {
    private Map<EntityRef, ModelInstance> modelBaseInstances = new HashMap<>();
    private Map<EntityRef, ModelInstance> modelLightInstances = new HashMap<>();

    private Model model;

    @Override
    public void destroy() {
        model.dispose();
    }

    @Override
    public void initialize() {
        Material material = new Material();

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(1, 1, 1, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked);
    }

    @ReceiveEvent
    public void objectAdded(AfterComponentAdded event, EntityRef entity, SignalIndicatorComponent modelColoredShape) {
        modelBaseInstances.put(entity, new ModelInstance(model));
        modelLightInstances.put(entity, new ModelInstance(model));
    }

    @ReceiveEvent
    public void objectRemoved(BeforeComponentRemoved event, EntityRef entity, SignalIndicatorComponent modelRender) {
        modelBaseInstances.remove(entity);
        modelLightInstances.remove(entity);
    }

    @ReceiveEvent
    public void returnShapeModelInstance(GetModelInstance event, EntityRef entityRef, SignalIndicatorComponent signalIndicator, SignalConsumerComponent signalConsumer) {
        appendBase(event, entityRef, signalIndicator);
        appendSignal(event, entityRef, signalIndicator, signalConsumer);
    }

    private void appendBase(GetModelInstance event, EntityRef entityRef, SignalIndicatorComponent signalIndicator) {
        ModelInstance baseResult = modelBaseInstances.get(entityRef);

        for (Material material : baseResult.materials) {
            material.set(ColorAttribute.createDiffuse(signalIndicator.getBaseColor()));
        }

        baseResult.transform.idt().translate(event.getLocation());
        baseResult.transform.rotate(0, 1, 0, event.getRotationY());

        baseResult.transform.translate(
                0.5f,
                0.5f,
                0.2f);

        baseResult.transform.scale(
                event.getScale().x * 0.4f,
                event.getScale().y * 0.4f,
                event.getScale().z * 0.2f);

        event.appendModelInstance(baseResult);
    }

    private void appendSignal(GetModelInstance event, EntityRef entityRef, SignalIndicatorComponent signalIndicator, SignalConsumerComponent signalConsumer) {
        ModelInstance lightResult = modelLightInstances.get(entityRef);

        for (Material material : lightResult.materials) {
            if (signalConsumer.isReceivingSignal()) {
                material.set(ColorAttribute.createDiffuse(signalIndicator.getSignalOnColor()));
                material.set(new ColorAttribute(ColorAttribute.Emissive, signalIndicator.getSignalOnColor()));
            } else {
                material.remove(ColorAttribute.Emissive);
                material.set(ColorAttribute.createDiffuse(signalIndicator.getSignalOffColor()));
            }
        }

        lightResult.transform.idt().translate(event.getLocation());
        lightResult.transform.rotate(0, 1, 0, event.getRotationY());

        lightResult.transform.translate(
                0.5f,
                0.5f,
                0.4f);

        lightResult.transform.scale(
                event.getScale().x * 0.2f,
                event.getScale().y * 0.2f,
                event.getScale().z * 0.1f);

        event.appendModelInstance(lightResult);
    }
}
