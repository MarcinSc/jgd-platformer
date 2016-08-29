package jgd.platformer.gameplay.rendering.model.g3d.activate;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.logic.activate.ActivateEntity;
import jgd.platformer.gameplay.rendering.model.g3d.PlayAnimation;

@RegisterSystem(
        profiles = "gameScreen"
)
public class G3DModelActivateSystem {
    @ReceiveEvent
    public void activateEntity(ActivateEntity activateEntity, EntityRef entityRef, G3DModelActivateComponent g3DModelActivate) {
        boolean activated = g3DModelActivate.isActivated();
        if (activated) {
            entityRef.send(new PlayAnimation(g3DModelActivate.getDeactivateAnimation(), g3DModelActivate.getDeactivateAnimationSpeed(), 0.1f, 1));
        } else {
            entityRef.send(new PlayAnimation(g3DModelActivate.getActivateAnimation(), g3DModelActivate.getActivateAnimationSpeed(), 0.1f, 1));
        }
        g3DModelActivate.setActivated(!activated);
        entityRef.saveChanges();
    }
}
