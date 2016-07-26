package jgd.platformer.gameplay.logic.ai;

import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.EntityRefReference;
import com.gempukku.gaming.ai.builder.TaskBuilder;
import com.gempukku.gaming.ai.task.AbstractAITask;
import com.gempukku.secsy.entity.EntityRef;

import java.util.Map;

public class FlipFacingDirectionTask extends AbstractAITask<EntityRefReference> {
    public FlipFacingDirectionTask(String id, AITask parent, TaskBuilder taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        FacingDirectionComponent component = entityRef.getComponent(FacingDirectionComponent.class);
        component.setDirection(component.getDirection().equals("right") ? "left" : "right");
        entityRef.saveChanges();
        return AITaskResult.SUCCESS;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        return null;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {

    }
}
