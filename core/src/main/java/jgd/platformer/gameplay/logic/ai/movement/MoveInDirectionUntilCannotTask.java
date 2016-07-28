package jgd.platformer.gameplay.logic.ai.movement;

import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.EntityRefReference;
import com.gempukku.gaming.ai.builder.TaskBuilder;
import com.gempukku.gaming.ai.task.AbstractAITask;
import com.gempukku.secsy.entity.EntityRef;

import java.util.Map;

public class MoveInDirectionUntilCannotTask extends AbstractAITask<EntityRefReference> {
    private static final String CANCELLED_KEY = "cancelled";

    public MoveInDirectionUntilCannotTask(String id, AITask parent, TaskBuilder taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        entityRef.createComponent(AIApplyMovementIfPossibleComponent.class);
        entityRef.saveChanges();
        return AITaskResult.RUNNING;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        if (reference.getValue(getId(), CANCELLED_KEY, Boolean.class) != null) {
            reference.removeValue(getId(), CANCELLED_KEY);

            EntityRef entityRef = reference.getEntityRef();
            entityRef.removeComponents(AIApplyMovementIfPossibleComponent.class);
            entityRef.saveChanges();

            return AITaskResult.SUCCESS;
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {
        reference.removeValue(getId(), CANCELLED_KEY);

        EntityRef entityRef = reference.getEntityRef();
        entityRef.removeComponents(AIApplyMovementIfPossibleComponent.class);
        entityRef.saveChanges();
    }

    public void notifyCantMove(EntityRefReference reference) {
        reference.setValue(getId(), CANCELLED_KEY, true);
        reference.storeValues();
    }
}
