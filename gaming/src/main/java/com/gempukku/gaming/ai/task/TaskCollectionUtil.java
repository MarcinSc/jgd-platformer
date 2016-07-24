package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.builder.TaskBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskCollectionUtil {

    private TaskCollectionUtil() {
    }

    public static List<AITask> buildTasks(AITask parent, TaskBuilder taskBuilder, List<Map<String, Object>> taskList) {
        List<AITask> tasks = new ArrayList<>(taskList.size());
        for (Map<String, Object> taskInfo : taskList) {
            tasks.add(taskBuilder.buildTask(parent, taskInfo));
        }
        return tasks;
    }
}
