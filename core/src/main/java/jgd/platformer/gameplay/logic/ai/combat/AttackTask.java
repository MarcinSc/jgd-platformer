package jgd.platformer.gameplay.logic.ai.combat;

import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.EntityRefReference;
import com.gempukku.gaming.ai.builder.TaskBuilder;
import com.gempukku.gaming.ai.task.AbstractAITask;

import java.util.Map;

public class AttackTask extends AbstractAITask<EntityRefReference> {
    public AttackTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        reference.getEntityRef().send(new PerformAttack());
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
