package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskCollectionUtil {

    private TaskCollectionUtil() {
    }

    public static <Reference extends AIReference> List<AITask<Reference>> buildTasks(AITask parent, TaskBuilder<Reference> taskBuilder, List<Map<String, Object>> taskList) {
        List<AITask<Reference>> tasks = new ArrayList<>(taskList.size());
        tasks.addAll(taskList.stream().map(taskInfo -> taskBuilder.buildTask(parent, taskInfo)).collect(Collectors.toList()));
        return tasks;
    }
}
