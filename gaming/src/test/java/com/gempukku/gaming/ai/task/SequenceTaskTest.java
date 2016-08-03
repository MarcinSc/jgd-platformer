package com.gempukku.gaming.ai.task;

import com.gempukku.gaming.ai.AIReference;
import com.gempukku.gaming.ai.AITask;
import com.gempukku.gaming.ai.AITaskResult;
import com.gempukku.gaming.ai.builder.TaskBuilder;
import com.gempukku.gaming.ai.map.MapAIReference;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SequenceTaskTest {
    @Test
    public void singlePassWithSuccess() {
        AIReference reference = new SampleMapAIReference();

        AITask task1 = Mockito.mock(AITask.class);
        AITask task2 = Mockito.mock(AITask.class);

        Mockito.when(task1.startTask(reference)).thenReturn(AITaskResult.SUCCESS);
        Mockito.when(task2.startTask(reference)).thenReturn(AITaskResult.SUCCESS);

        SequenceTask task = sequenceTask(task1, task2);

        assertEquals(AITaskResult.SUCCESS, task.startTask(reference));
        Mockito.verify(task1).startTask(reference);
        Mockito.verify(task2).startTask(reference);

        Mockito.verifyNoMoreInteractions(task1, task2);
    }

    @Test
    public void singlePassSecondFailure() {
        AIReference reference = new SampleMapAIReference();

        AITask task1 = Mockito.mock(AITask.class);
        AITask task2 = Mockito.mock(AITask.class);

        Mockito.when(task1.startTask(reference)).thenReturn(AITaskResult.SUCCESS);
        Mockito.when(task2.startTask(reference)).thenReturn(AITaskResult.FAILURE);

        SequenceTask task = sequenceTask(task1, task2);

        assertEquals(AITaskResult.FAILURE, task.startTask(reference));
        Mockito.verify(task1).startTask(reference);
        Mockito.verify(task2).startTask(reference);

        Mockito.verifyNoMoreInteractions(task1, task2);
    }

    @Test
    public void singlePassFirstFailure() {
        AIReference reference = new SampleMapAIReference();

        AITask task1 = Mockito.mock(AITask.class);
        AITask task2 = Mockito.mock(AITask.class);

        Mockito.when(task1.startTask(reference)).thenReturn(AITaskResult.FAILURE);

        SequenceTask task = sequenceTask(task1, task2);

        assertEquals(AITaskResult.FAILURE, task.startTask(reference));
        Mockito.verify(task1).startTask(reference);

        Mockito.verifyNoMoreInteractions(task1, task2);
    }

    @Test
    public void continueFirstPass() {
        AIReference reference = new SampleMapAIReference();

        AITask task1 = Mockito.mock(AITask.class);
        AITask task2 = Mockito.mock(AITask.class);

        Mockito.when(task1.startTask(reference)).thenReturn(AITaskResult.RUNNING);
        Mockito.when(task1.continueTask(reference)).thenReturn(AITaskResult.SUCCESS);
        Mockito.when(task2.startTask(reference)).thenReturn(AITaskResult.SUCCESS);

        SequenceTask task = sequenceTask(task1, task2);

        assertEquals(AITaskResult.RUNNING, task.startTask(reference));
        Mockito.verify(task1).startTask(reference);

        Mockito.verifyNoMoreInteractions(task1, task2);

        assertEquals(AITaskResult.SUCCESS, task.continueTask(reference));
        Mockito.verify(task1).continueTask(reference);
        Mockito.verify(task2).startTask(reference);

        Mockito.verifyNoMoreInteractions(task1, task2);
    }

    @Test
    public void continueSecondPass() {
        AIReference reference = new SampleMapAIReference();

        AITask task1 = Mockito.mock(AITask.class);
        AITask task2 = Mockito.mock(AITask.class);

        Mockito.when(task1.startTask(reference)).thenReturn(AITaskResult.SUCCESS);
        Mockito.when(task2.startTask(reference)).thenReturn(AITaskResult.RUNNING);
        Mockito.when(task2.continueTask(reference)).thenReturn(AITaskResult.SUCCESS);

        SequenceTask task = sequenceTask(task1, task2);

        assertEquals(AITaskResult.RUNNING, task.startTask(reference));
        Mockito.verify(task1).startTask(reference);
        Mockito.verify(task2).startTask(reference);

        Mockito.verifyNoMoreInteractions(task1, task2);

        assertEquals(AITaskResult.SUCCESS, task.continueTask(reference));
        Mockito.verify(task2).continueTask(reference);

        Mockito.verifyNoMoreInteractions(task1, task2);
    }

    private SequenceTask sequenceTask(AITask... tasks) {
        TaskBuilder taskBuilder = Mockito.mock(TaskBuilder.class);
        OngoingStubbing<AITask> stubbing = Mockito.when(taskBuilder.buildTask(Mockito.any(), Mockito.any()));
        List<Map<String, Object>> list = new LinkedList<>();
        for (AITask task : tasks) {
            stubbing = stubbing.thenReturn(task);
            list.add(new HashMap<>());
        }

        return new SequenceTask("id", null, taskBuilder, Collections.singletonMap("tasks", list));
    }

    private class SampleMapAIReference extends MapAIReference {
        public SampleMapAIReference() {
            super(new HashMap<>());
        }

        @Override
        public void storeValues() {

        }
    }
}