package jgd.platformer.gameplay.rendering.model.g3d;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import jgd.platformer.gameplay.logic.physics.ModelIdles;
import jgd.platformer.gameplay.logic.physics.ModelWalks;
import jgd.platformer.gameplay.rendering.model.GetModelInstance;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameplay"
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
    public void modelWalks(ModelWalks event, EntityRef entity, G3DModelAnimationComponent animation) {
        String playedAnimation = animation.getPlayedAnimation();
        String walkAnimation = animation.getWalkAnimation();
        if (playedAnimation == null || !playedAnimation.equals(walkAnimation)) {
            float walkAnimationSpeed = animation.getWalkAnimationSpeed();

            animation.setPlayedAnimation(walkAnimation);
            entity.saveChanges();

            AnimationController animationController = modelAnimations.get(entity);
            animationController.animate(walkAnimation, -1, walkAnimationSpeed, null, 0.1f);
        }
    }

    @ReceiveEvent
    public void modelIdles(ModelIdles event, EntityRef entity, G3DModelAnimationComponent animation) {
        String playedAnimation = animation.getPlayedAnimation();
        String idleAnimation = animation.getIdleAnimation();
        if (playedAnimation == null || !playedAnimation.equals(idleAnimation)) {
            float idleAnimationSpeed = animation.getIdleAnimationSpeed();

            animation.setPlayedAnimation(idleAnimation);
            entity.saveChanges();

            AnimationController animationController = modelAnimations.get(entity);
            animationController.animate(idleAnimation, -1, idleAnimationSpeed, null, 0.1f);
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

        result.transform.idt();
        if (animationController != null) {
            float seconds = timeManager.getTimeSinceLastUpdate() / 1000f;
            animationController.update(seconds);
        }
        result.transform.translate(event.getLocation());
        result.transform.rotate(0, 1, 0, event.getRotationY());

        result.transform.scale(
                event.getScale().x * g3dModel.getScaleX(),
                event.getScale().y * g3dModel.getScaleY(),
                event.getScale().z * g3dModel.getScaleZ());

        event.appendModelInstance(result);
    }
}
