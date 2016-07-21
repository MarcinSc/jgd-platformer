package com.gempukku.secsy.entity;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.context.util.PriorityCollection;
import com.gempukku.secsy.entity.component.ComponentManager;
import com.gempukku.secsy.entity.component.InternalComponentManager;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.AfterEntityLoaded;
import com.gempukku.secsy.entity.event.BeforeEntityUnloaded;
import com.gempukku.secsy.entity.event.Event;
import com.gempukku.secsy.entity.game.InternalGameLoop;
import com.gempukku.secsy.entity.game.InternalGameLoopListener;
import com.gempukku.secsy.entity.io.EntityData;
import com.gempukku.secsy.entity.relevance.EntityRelevanceRule;
import com.gempukku.secsy.entity.relevance.EntityRelevanceRuleRegistry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.*;

@RegisterSystem(profiles = "simpleEntityManager", shared = {EntityManager.class, InternalEntityManager.class, EntityRelevanceRuleRegistry.class})
public class SimpleEntityManager implements EntityManager, InternalEntityManager,
        EntityRelevanceRuleRegistry, LifeCycleSystem, InternalGameLoopListener {
    @Inject
    private ComponentManager componentManager;
    @Inject
    private InternalComponentManager internalComponentManager;
    @Inject
    private InternalGameLoop internalGameLoop;

    private PriorityCollection<EntityEventListener> entityEventListeners = new PriorityCollection<>();
    private PriorityCollection<EntityListener> entityListeners = new PriorityCollection<>();
    private Set<EntityRelevanceRule> entityRelevanceRules = new HashSet<>();

    private int maxId;
    private Set<SimpleEntity> entities = new HashSet<>();

    private EntityListener dispatchEntityListener = new DispatchEntityListener();
    private EntityEventListener dispatchEntityEventListener = new DispatchEntityEventListener();

    @Override
    public void addEntityEventListener(EntityEventListener entityEventListener) {
        entityEventListeners.add(entityEventListener);
    }

    @Override
    public void removeEntityEventListener(EntityEventListener entityEventListener) {
        entityEventListeners.remove(entityEventListener);
    }

    @Override
    public void addEntityListener(EntityListener entityListener) {
        entityListeners.add(entityListener);
    }

    @Override
    public void removeEntityListener(EntityListener entityListener) {
        entityListeners.remove(entityListener);
    }

    @Override
    public void registerEntityRelevanceRule(EntityRelevanceRule entityRelevanceRule) {
        entityRelevanceRules.add(entityRelevanceRule);
    }

    @Override
    public void deregisterEntityRelevanceRule(EntityRelevanceRule entityRelevanceRule) {
        entityRelevanceRules.remove(entityRelevanceRule);
    }

    @Override
    public void initialize() {
        internalGameLoop.addInternalGameLoopListener(this);
    }

    @Override
    public void destroy() {
        internalGameLoop.removeInternalGameLooplListener(this);
    }

    @Override
    public void preUpdate() {
        // Do nothing
    }

    /**
     * This method unloads all irrelevant entities
     */
    @Override
    public void postUpdate() {
        // First go through all the registered rules and tell them to update
        // their internal rules
        entityRelevanceRules.forEach(EntityRelevanceRule::determineRelevance);

        // Determine, which entities are unloaded for which rule
        Multimap<EntityRelevanceRule, SimpleEntity> entitiesToUnloadByRules = determineEntitiesToUnloadByRules();

        // Pass the entities to their rules to store them before unload
        tellRulesToStoreUnloadingEntities(entitiesToUnloadByRules);

        // Send events to them
        Collection<SimpleEntity> entitiesToUnload = entitiesToUnloadByRules.values();
        notifyEntitiesTheyAreBeingUnloaded(entitiesToUnload);

        // Unload the entities
        unloadTheEntities(entitiesToUnload);

        entityListeners.forEach(
                listener -> listener.entitiesModified(entitiesToUnload));

        int lastMaxId = maxId;

        // Load any new entities that became relevant
        Set<SimpleEntity> loadedEntities = loadNewlyRelevantEntities();

        entityListeners.forEach(
                listener -> listener.entitiesModified(loadedEntities));

        // Send events to them
        sendEventsToThem(loadedEntities, lastMaxId);

        entityRelevanceRules.forEach(EntityRelevanceRule::newRelevantEntitiesLoaded);
    }

    @Override
    public int getEntityId(EntityRef entityRef) {
        return ((SimpleEntityRef) entityRef).getEntity().getEntityId();
    }

    @Override
    public String getEntityUniqueIdentifier(EntityRef entityRef) {
        return String.valueOf(getEntityId(entityRef));
    }

    private void sendEventsToThem(Set<SimpleEntity> loadedEntities, int createdIfIdGreaterThan) {
        for (SimpleEntity entity : loadedEntities) {
            Map<Class<? extends Component>, Component> components = new HashMap<>();
            for (Map.Entry<Class<? extends Component>, Component> originalComponents : entity.entityValues.entrySet()) {
                components.put(originalComponents.getKey(), internalComponentManager.copyComponentUnmodifiable(originalComponents.getValue(), false));
            }

            SimpleEntityRef entityRef = createSimpleEntityRef(entity, false);
            if (entity.getEntityId() <= createdIfIdGreaterThan) {
                entityRef.send(new AfterEntityLoaded(components));
            } else {
                entityRef.send(new AfterComponentAdded(components));
            }
        }
    }

    private Set<SimpleEntity> loadNewlyRelevantEntities() {
        Set<SimpleEntity> loadedEntities = new HashSet<>();
        for (EntityRelevanceRule entityRelevanceRule : entityRelevanceRules) {
            entityRelevanceRule.getNewRelevantEntities().forEach(
                    entityData -> {
                        int id = entityData.getEntityId();
                        if (id == 0)
                            id = ++maxId;
                        SimpleEntity entity = new SimpleEntity(internalComponentManager, id);
                        addEntityDataToEntity(entityData, entity);
                        entities.add(entity);
                        loadedEntities.add(entity);
                    });
        }
        return loadedEntities;
    }

    private void addEntityDataToEntity(EntityData entityData, SimpleEntity entity) {
        entityData.getComponents().forEach(
                componentData -> {
                    Class<? extends Component> componentClass = componentData.getComponentClass();
                    Component component = internalComponentManager.createComponent(null, componentClass);
                    componentData.getFields().entrySet().forEach(
                            fieldNameAndValue -> internalComponentManager.setComponentFieldValue(component, fieldNameAndValue.getKey(), fieldNameAndValue.getValue()));
                    entity.entityValues.put(componentClass, component);
                });
    }

    private void unloadTheEntities(Collection<SimpleEntity> entitiesToUnload) {
        entitiesToUnload.forEach(
                entity -> {
                    entity.exists = false;
                    entities.remove(entity);
                });
    }

    private void notifyEntitiesTheyAreBeingUnloaded(Collection<SimpleEntity> entitiesToUnload) {
        entitiesToUnload.forEach(
                entity -> {
                    Map<Class<? extends Component>, Component> components = new HashMap<>();
                    for (Map.Entry<Class<? extends Component>, Component> originalComponents : entity.entityValues.entrySet()) {
                        components.put(originalComponents.getKey(), internalComponentManager.copyComponentUnmodifiable(originalComponents.getValue(), false));
                    }

                    createSimpleEntityRef(entity, false).send(new BeforeEntityUnloaded(components));
                });
    }

    private void tellRulesToStoreUnloadingEntities(Multimap<EntityRelevanceRule, SimpleEntity> entitiesToUnload) {
        for (Map.Entry<EntityRelevanceRule, Collection<SimpleEntity>> ruleEntities : entitiesToUnload.asMap().entrySet()) {
            EntityRelevanceRule rule = ruleEntities.getKey();
            rule.storeEntities(ruleEntities.getValue());
        }
    }

    private Multimap<EntityRelevanceRule, SimpleEntity> determineEntitiesToUnloadByRules() {
        Multimap<EntityRelevanceRule, SimpleEntity> entitiesToUnload = HashMultimap.create();
        for (EntityRelevanceRule entityRelevanceRule : entityRelevanceRules) {
            for (EntityRef entityRef : entityRelevanceRule.getNotRelevantEntities()) {
                entitiesToUnload.put(entityRelevanceRule, ((SimpleEntityRef) entityRef).getEntity());
            }
        }
        return entitiesToUnload;
    }

    @Override
    public EntityRef createEntity() {
        SimpleEntity entity = new SimpleEntity(internalComponentManager, ++maxId);
        entities.add(entity);
        return createSimpleEntityRef(entity, false);
    }

    @Override
    public EntityRef createEntity(EntityData entityData) {
        SimpleEntity entity = new SimpleEntity(internalComponentManager, ++maxId);
        addEntityDataToEntity(entityData, entity);
        entities.add(entity);

        Map<Class<? extends Component>, Component> components = new HashMap<>();
        entity.entityValues.forEach(
                (clazz, component) -> components.put(clazz, internalComponentManager.copyComponentUnmodifiable(component, false)));

        entityListeners.forEach(
                listener -> listener.entitiesModified(Collections.singleton(entity)));

        SimpleEntityRef entityRef = createSimpleEntityRef(entity, false);
        dispatchEntityEventListener.eventSent(entityRef, new AfterComponentAdded(components));
        return entityRef;
    }

    @Override
    public EntityRef wrapEntityData(EntityData entityData) {
        SimpleEntity entity = new SimpleEntity(internalComponentManager, 0);
        entity.exists = false;
        addEntityDataToEntity(entityData, entity);
        return createSimpleEntityRef(entity, true);
    }

    @Override
    public EntityRef createNewEntityRef(EntityRef entityRef) {
        return createSimpleEntityRef(((SimpleEntityRef) entityRef).getEntity(), false);
    }

    @Override
    public boolean isSameEntity(EntityRef ref1, EntityRef ref2) {
        return ((SimpleEntityRef) ref1).getEntity() == ((SimpleEntityRef) ref2).getEntity();
    }

    @Override
    public EntityRef wrapEntityStub(SimpleEntity entity) {
        return createSimpleEntityRef(entity, false);
    }

    @Override
    public void destroyEntity(EntityRef entityRef) {
        Iterable<Class<? extends Component>> components = entityRef.listComponents();
        //noinspection unchecked
        entityRef.removeComponents(Iterables.toArray(components, Class.class));
        entityRef.saveChanges();
        SimpleEntity underlyingEntity = ((SimpleEntityRef) entityRef).getEntity();
        underlyingEntity.exists = false;
        entities.remove(underlyingEntity);
    }

    @Override
    public Iterable<EntityRef> getEntitiesWithComponents(Class<? extends Component> component, Class<? extends Component>... additionalComponents) {
        return Iterables.transform(Iterables.filter(entities,
                entity -> {
                    if (!entity.entityValues.containsKey(component))
                        return false;

                    for (Class<? extends Component> additionalComponent : additionalComponents) {
                        if (!entity.entityValues.containsKey(additionalComponent))
                            return false;
                    }

                    return true;
                }),
                entity -> createSimpleEntityRef(entity, false));
    }

    @Override
    public Iterable<EntityRef> getAllEntities() {
        return Iterables.transform(new HashSet<>(entities),
                entity -> createSimpleEntityRef(entity, false));
    }

    private SimpleEntityRef createSimpleEntityRef(SimpleEntity entity, boolean readOnly) {
        return new SimpleEntityRef(internalComponentManager, dispatchEntityListener, dispatchEntityEventListener,
                entity, readOnly);
    }

    private class DispatchEntityListener implements EntityListener {
        @Override
        public void entitiesModified(Iterable<SimpleEntity> entity) {
            entityListeners.forEach(
                    listener -> listener.entitiesModified(entity));
        }
    }

    private class DispatchEntityEventListener implements EntityEventListener {
        @Override
        public void eventSent(EntityRef entity, Event event) {
            entityEventListeners.forEach(
                    listener -> {
                        SimpleEntityRef newEntityRef = createSimpleEntityRef(((SimpleEntityRef) entity).getEntity(), false);
                        listener.eventSent(newEntityRef, event);
                    });
        }
    }
}
