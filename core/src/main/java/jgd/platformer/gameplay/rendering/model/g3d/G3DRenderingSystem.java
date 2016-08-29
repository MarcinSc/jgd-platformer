package jgd.platformer.gameplay.rendering.model.g3d;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import jgd.platformer.gameplay.rendering.model.GetModelInstance;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameScreen"
)
public class G3DRenderingSystem implements LifeCycleSystem {
    @Inject
    private TimeManager timeManager;

    private Map<String, Model> models = new HashMap<>();
    private Map<EntityRef, ModelInstance> modelInstances = new HashMap<>();
    private Map<EntityRef, AnimationController> modelAnimations = new HashMap<>();

    @Override
    public void destroy() {
        for (Model model : models.values()) {
            model.dispose();
        }
    }

    @ReceiveEvent
    public void objectAdded(AfterComponentAdded event, EntityRef entity, G3DModelComponent g3dModel) {
        String modelFile = g3dModel.getModelFile();
        if (!models.containsKey(modelFile)) {
            AssetManager assetManager = new AssetManager();
            assetManager.load(modelFile, Model.class);
            assetManager.finishLoading();

            Model model = assetManager.get(modelFile, Model.class);
            models.put(modelFile, model);
        }

        ModelInstance modelInstance = new ModelInstance(models.get(modelFile));
        modelInstances.put(entity, modelInstance);

        G3DModelAnimationComponent animation = entity.getComponent(G3DModelAnimationComponent.class);
        if (animation != null) {
            AnimationController animationController = new AnimationController(modelInstance);
            modelAnimations.put(entity, animationController);
        }
    }

    @ReceiveEvent
    public void objectRemoved(BeforeComponentRemoved event, EntityRef entity, G3DModelComponent modelRender) {
        modelInstances.remove(entity);
        modelAnimations.remove(entity);
    }

    @ReceiveEvent
    public void playAnimation(PlayAnimation event, EntityRef entity, G3DModelAnimationComponent animation) {
        String playedAnimation = animation.getPlayedAnimation();
        String animationName = event.getAnimationName();
        if (playedAnimation == null || !playedAnimation.equals(animationName)) {
            float speedMultiplier = event.getSpeedMultiplier();

            animation.setPlayedAnimation(animationName);
            entity.saveChanges();

            AnimationController animationController = modelAnimations.get(entity);
            animationController.animate(animationName, event.getLoopCount(), speedMultiplier, null, event.getTransitionTime());
        }
    }

    @ReceiveEvent
    public void returnShapeModelInstance(GetModelInstance event, EntityRef entityRef, G3DModelComponent g3dModel) {
        AnimationController animationController = modelAnimations.get(entityRef);

        ModelInstance result = modelInstances.get(entityRef);

        for (Material material : result.materials) {
            float opacity = g3dModel.getOpacity();
            if (opacity == 1) {
                material.remove(BlendingAttribute.Type);
            } else {
                material.set(new BlendingAttribute(true, opacity));
            }
        }

        G3DModelColorComponent color = entityRef.getComponent(G3DModelColorComponent.class);
        if (color != null) {
            Material material = result.getMaterial(color.getMaterialId());
            material.set(ColorAttribute.createDiffuse(
                    color.getRed() / 255f,
                    color.getGreen() / 255f,
                    color.getBlue() / 255f,
                    1f));
        }

        result.transform.idt();
        if (animationController != null) {
            float seconds = timeManager.getTimeSinceLastUpdate() / 1000f;
            animationController.update(seconds);
        }
        result.transform.translate(
                event.getLocation().x + g3dModel.getTranslateX(),
                event.getLocation().y + g3dModel.getTranslateY(),
                event.getLocation().z + g3dModel.getTranslateZ());
        result.transform.rotate(0, 1, 0, event.getRotationY());

        result.transform.scale(
                event.getScale().x * g3dModel.getScaleX(),
                event.getScale().y * g3dModel.getScaleY(),
                event.getScale().z * g3dModel.getScaleZ());

        event.appendModelInstance(result);
    }
}
