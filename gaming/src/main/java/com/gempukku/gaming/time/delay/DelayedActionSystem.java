package com.gempukku.gaming.time.delay;

import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterEntityLoaded;
import com.gempukku.secsy.entity.event.BeforeEntityUnloaded;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RegisterSystem(
        profiles = {"delayActions"}, shared = DelayManager.class)
public class DelayedActionSystem implements DelayManager {
    @Inject
    private TimeManager timeManager;

    private SortedSetMultimap<Long, EntityRef> delayedOperationsSortedByTime = TreeMultimap.create(Ordering.natural(), Ordering.arbitrary());
    private SortedSetMultimap<Long, EntityRef> periodicOperationsSortedByTime = TreeMultimap.create(Ordering.natural(), Ordering.arbitrary());

    @ReceiveEvent
    public void invokeScheduledActions(GameLoopUpdate gameLoopUpdate, EntityRef entity) {
        final long currentTime = timeManager.getTime();
        invokeDelayedOperations(currentTime);
        invokePeriodicOperations(currentTime);
    }

    private void invokeDelayedOperations(long currentWorldTime) {
        List<EntityRef> operationsToInvoke = new LinkedList<>();
        Iterator<Long> scheduledOperationsIterator = delayedOperationsSortedByTime.keySet().iterator();
        long processedTime;
        while (scheduledOperationsIterator.hasNext()) {
            processedTime = scheduledOperationsIterator.next();
            if (processedTime > currentWorldTime) {
                break;
            }
            operationsToInvoke.addAll(delayedOperationsSortedByTime.get(processedTime));
            scheduledOperationsIterator.remove();
        }

        operationsToInvoke.stream().filter(EntityRef::exists).forEach(delayedEntity -> {
            final DelayedActionComponent delayedActions = delayedEntity.getComponent(DelayedActionComponent.class);

            final Set<String> actionIds = removeActionsUpTo(delayedActions, currentWorldTime);
            saveOrRemoveComponent(delayedEntity, delayedActions);
            delayedEntity.saveChanges();

            if (!delayedActions.getActionIdWakeUp().isEmpty()) {
                delayedOperationsSortedByTime.put(findSmallestWakeUp(delayedActions.getActionIdWakeUp()), delayedEntity);
            }

            for (String actionId : actionIds) {
                delayedEntity.send(new DelayedActionTriggeredEvent(actionId));
            }
        });
    }

    private void invokePeriodicOperations(long currentWorldTime) {
        List<EntityRef> operationsToInvoke = new LinkedList<>();
        Iterator<Long> scheduledOperationsIterator = periodicOperationsSortedByTime.keySet().iterator();
        long processedTime;
        while (scheduledOperationsIterator.hasNext()) {
            processedTime = scheduledOperationsIterator.next();
            if (processedTime > currentWorldTime) {
                break;
            }
            operationsToInvoke.addAll(periodicOperationsSortedByTime.get(processedTime));
            scheduledOperationsIterator.remove();
        }

        operationsToInvoke.stream().filter(EntityRef::exists).forEach(periodicEntity -> {
            final PeriodicActionComponent periodicActionComponent = periodicEntity.getComponent(PeriodicActionComponent.class);

            final Set<String> actionIds = getTriggeredActionsAndReschedule(periodicActionComponent, currentWorldTime);
            saveOrRemoveComponent(periodicEntity, periodicActionComponent);
            periodicEntity.saveChanges();

            if (!periodicActionComponent.getActionIdWakeUp().isEmpty()) {
                periodicOperationsSortedByTime.put(findSmallestWakeUp(periodicActionComponent.getActionIdWakeUp()), periodicEntity);
            }

            for (String actionId : actionIds) {
                periodicEntity.send(new PeriodicActionTriggeredEvent(actionId));
            }
        });
    }

    @ReceiveEvent
    public void delayedComponentActivated(AfterEntityLoaded event, EntityRef entity, DelayedActionComponent delayedActionComponent) {
        delayedOperationsSortedByTime.put(findSmallestWakeUp(delayedActionComponent.getActionIdWakeUp()), entity);
    }

    @ReceiveEvent
    public void periodicComponentActivated(AfterEntityLoaded event, EntityRef entity, PeriodicActionComponent periodicActionComponent) {
        periodicOperationsSortedByTime.put(findSmallestWakeUp(periodicActionComponent.getActionIdWakeUp()), entity);
    }

    @ReceiveEvent
    public void delayedComponentDeactivated(BeforeEntityUnloaded event, EntityRef entity, DelayedActionComponent delayedActionComponent) {
        delayedOperationsSortedByTime.remove(findSmallestWakeUp(delayedActionComponent.getActionIdWakeUp()), entity);
    }

    @ReceiveEvent
    public void periodicComponentDeactivated(BeforeEntityUnloaded event, EntityRef entity, PeriodicActionComponent periodicActionComponent) {
        delayedOperationsSortedByTime.remove(findSmallestWakeUp(periodicActionComponent.getActionIdWakeUp()), entity);
    }

    @Override
    public void addDelayedAction(EntityRef entity, String actionId, long delay) {
        long scheduleTime = timeManager.getTime() + delay;

        DelayedActionComponent delayedActionComponent = entity.getComponent(DelayedActionComponent.class);
        if (delayedActionComponent != null) {
            final long oldWakeUp = findSmallestWakeUp(delayedActionComponent.getActionIdWakeUp());
            delayedActionComponent.getActionIdWakeUp().put(actionId, scheduleTime);
            final long newWakeUp = findSmallestWakeUp(delayedActionComponent.getActionIdWakeUp());
            entity.saveChanges();
            if (oldWakeUp < newWakeUp) {
                delayedOperationsSortedByTime.remove(oldWakeUp, entity);
                delayedOperationsSortedByTime.put(newWakeUp, entity);
            }
        } else {
            delayedActionComponent = entity.createComponent(DelayedActionComponent.class);
            Map<String, Long> wakeUps = new HashMap<>();
            wakeUps.put(actionId, scheduleTime);
            delayedActionComponent.setActionIdWakeUp(wakeUps);
            entity.saveChanges();
            delayedOperationsSortedByTime.put(scheduleTime, entity);
        }
    }

    @Override
    public void addPeriodicAction(EntityRef entity, String actionId, long initialDelay, long period) {
        long scheduleTime = timeManager.getTime() + initialDelay;

        PeriodicActionComponent periodicActionComponent = entity.getComponent(PeriodicActionComponent.class);
        if (periodicActionComponent != null) {
            final long oldWakeUp = findSmallestWakeUp(periodicActionComponent.getActionIdWakeUp());
            periodicActionComponent.getActionIdWakeUp().put(actionId, scheduleTime);
            periodicActionComponent.getActionIdPeriod().put(actionId, period);
            final long newWakeUp = findSmallestWakeUp(periodicActionComponent.getActionIdWakeUp());
            entity.saveChanges();
            if (oldWakeUp < newWakeUp) {
                periodicOperationsSortedByTime.remove(oldWakeUp, entity);
                periodicOperationsSortedByTime.put(newWakeUp, entity);
            }
        } else {
            periodicActionComponent = entity.createComponent(PeriodicActionComponent.class);
            Map<String, Long> wakeUps = new HashMap<>();
            wakeUps.put(actionId, scheduleTime);
            Map<String, Long> periods = new HashMap<>();
            periods.put(actionId, period);
            periodicActionComponent.setActionIdWakeUp(wakeUps);
            periodicActionComponent.setActionIdPeriod(periods);
            entity.saveChanges();
            periodicOperationsSortedByTime.put(scheduleTime, entity);
        }
    }

    @Override
    public void cancelDelayedAction(EntityRef entity, String actionId) {
        DelayedActionComponent delayedComponent = entity.getComponent(DelayedActionComponent.class);
        long oldWakeUp = findSmallestWakeUp(delayedComponent.getActionIdWakeUp());
        delayedComponent.getActionIdWakeUp().remove(actionId);
        long newWakeUp = findSmallestWakeUp(delayedComponent.getActionIdWakeUp());
        if (!delayedComponent.getActionIdWakeUp().isEmpty() && oldWakeUp < newWakeUp) {
            delayedOperationsSortedByTime.remove(oldWakeUp, entity);
            delayedOperationsSortedByTime.put(newWakeUp, entity);
        } else if (delayedComponent.getActionIdWakeUp().isEmpty()) {
            delayedOperationsSortedByTime.remove(oldWakeUp, entity);
        }
        saveOrRemoveComponent(entity, delayedComponent);
        entity.saveChanges();
    }

    @Override
    public void cancelPeriodicAction(EntityRef entity, String actionId) {
        PeriodicActionComponent periodicActionComponent = entity.getComponent(PeriodicActionComponent.class);
        long oldWakeUp = findSmallestWakeUp(periodicActionComponent.getActionIdWakeUp());
        periodicActionComponent.getActionIdWakeUp().remove(actionId);
        periodicActionComponent.getActionIdPeriod().remove(actionId);
        long newWakeUp = findSmallestWakeUp(periodicActionComponent.getActionIdWakeUp());
        if (!periodicActionComponent.getActionIdWakeUp().isEmpty() && oldWakeUp < newWakeUp) {
            periodicOperationsSortedByTime.remove(oldWakeUp, entity);
            periodicOperationsSortedByTime.put(newWakeUp, entity);
        } else if (periodicActionComponent.getActionIdWakeUp().isEmpty()) {
            periodicOperationsSortedByTime.remove(oldWakeUp, entity);
        }
        saveOrRemoveComponent(entity, periodicActionComponent);
        entity.saveChanges();
    }

    @Override
    public boolean hasDelayedAction(EntityRef entity, String actionId) {
        DelayedActionComponent delayedComponent = entity.getComponent(DelayedActionComponent.class);
        return delayedComponent != null && delayedComponent.getActionIdWakeUp().containsKey(actionId);
    }

    @Override
    public boolean hasPeriodicAction(EntityRef entity, String actionId) {
        PeriodicActionComponent periodicActionComponent = entity.getComponent(PeriodicActionComponent.class);
        return periodicActionComponent != null && periodicActionComponent.getActionIdWakeUp().containsKey(actionId);
    }

    private void saveOrRemoveComponent(EntityRef delayedEntity, DelayedActionComponent delayedActionComponent) {
        if (delayedActionComponent.getActionIdWakeUp().isEmpty()) {
            delayedEntity.removeComponents(DelayedActionComponent.class);
        }
    }

    private void saveOrRemoveComponent(EntityRef periodicEntity, PeriodicActionComponent periodicActionComponent) {
        if (periodicActionComponent.getActionIdWakeUp().isEmpty()) {
            periodicEntity.removeComponents(PeriodicActionComponent.class);
        }
    }

    private Set<String> removeActionsUpTo(DelayedActionComponent delayedActionComponent, final long worldTime) {
        Map<String, Long> actionIdWakeUp = delayedActionComponent.getActionIdWakeUp();

        final Set<String> result = new HashSet<>();
        final Iterator<Map.Entry<String, Long>> entryIterator = actionIdWakeUp.entrySet().iterator();
        while (entryIterator.hasNext()) {
            final Map.Entry<String, Long> entry = entryIterator.next();
            if (entry.getValue() <= worldTime) {
                result.add(entry.getKey());
                entryIterator.remove();
            }
        }

        return result;
    }

    private long findSmallestWakeUp(Map<String, Long> actionIdsWakeUp) {
        long result = Long.MAX_VALUE;
        for (long value : actionIdsWakeUp.values()) {
            result = Math.min(result, value);
        }
        return result;
    }

    private Set<String> getTriggeredActionsAndReschedule(PeriodicActionComponent periodicActionComponent, final long worldTime) {
        final Set<String> result = new HashSet<>();
        Map<String, Long> actionIdWakeUp = periodicActionComponent.getActionIdWakeUp();
        final Iterator<Map.Entry<String, Long>> entryIterator = actionIdWakeUp.entrySet().iterator();
        while (entryIterator.hasNext()) {
            final Map.Entry<String, Long> entry = entryIterator.next();
            if (entry.getValue() <= worldTime) {
                result.add(entry.getKey());
                entryIterator.remove();
            }
        }

        // Rescheduling
        for (String actionId : result) {
            actionIdWakeUp.put(actionId, worldTime + periodicActionComponent.getActionIdPeriod().get(actionId));
        }

        return result;
    }
}
