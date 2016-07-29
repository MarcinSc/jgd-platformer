package jgd.platformer.gameplay.logic.ai.combat;

import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.EntityRefReference;
import com.gempukku.gaming.ai.builder.TaskBuilder;
import com.gempukku.gaming.ai.task.AbstractAITask;
import com.gempukku.secsy.context.annotation.Inject;
import jgd.platformer.gameplay.logic.faction.FactionManager;

import java.util.Map;

public class FindEnemyInRangeTask extends AbstractAITask<EntityRefReference> {
    @Inject
    private FactionManager factionManager;

    public FindEnemyInRangeTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        if (factionManager.hasEnemy(reference.getEntityRef(), entityRef -> true))
            return AITaskResult.SUCCESS;
        else
            return AITaskResult.FAILURE;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        return null;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {

    }
}
