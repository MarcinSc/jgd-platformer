package com.gempukku.secsy.entity;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.ShareSystemInitializer;
import com.gempukku.secsy.entity.component.map.MapAnnotationDrivenProxyComponentManager;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.AfterComponentRemoved;
import com.gempukku.secsy.entity.event.AfterComponentUpdated;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import com.gempukku.secsy.entity.event.Event;
import com.gempukku.secsy.entity.game.InternalGameLoop;
import com.gempukku.secsy.entity.game.InternalGameLoopListener;
import com.gempukku.secsy.entity.io.ComponentData;
import com.gempukku.secsy.entity.io.EntityData;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SimpleEntityManagerTest {
    private SimpleEntityManager simpleEntityManager;

    @Before
    public void setup() {
        MapAnnotationDrivenProxyComponentManager componentManager = new MapAnnotationDrivenProxyComponentManager();
        simpleEntityManager = new SimpleEntityManager();
        ShareSystemInitializer<Object> shareSystemInitializer = new ShareSystemInitializer<>();
        Collection<Object> systems = Arrays.asList(componentManager, simpleEntityManager, new MockInternalGameLoop());
        Map<Class<?>, Object> systemMap = shareSystemInitializer.extractSystems(systems);
        shareSystemInitializer.initializeObjects(systems, systemMap);
    }

    @Test
    public void createEntity() {
        EntityRef entity = simpleEntityManager.createEntity();
        assertFalse(entity.hasComponent(SampleComponent.class));
    }

    @Test
    public void addComponentInteractingMultipleEntityRefs() {
        EntityRef source = simpleEntityManager.createEntity();

        EntityRef copy = simpleEntityManager.createNewEntityRef(source);
        SampleComponent component = copy.createComponent(SampleComponent.class);

        assertFalse(copy.hasComponent(SampleComponent.class));
        assertFalse(source.hasComponent(SampleComponent.class));

        copy.saveChanges();
        assertTrue(copy.hasComponent(SampleComponent.class));
        assertTrue(source.hasComponent(SampleComponent.class));
    }

    @Test
    public void addComponentWithSaveInteractingMultipleEntityRefs() {
        EntityRef source = simpleEntityManager.createEntity();

        EntityRef copy = simpleEntityManager.createNewEntityRef(source);
        SampleComponent component = copy.createComponent(SampleComponent.class);
        component.setValue("value");

        assertFalse(copy.hasComponent(SampleComponent.class));
        assertFalse(source.hasComponent(SampleComponent.class));

        copy.saveChanges();
        assertTrue(copy.hasComponent(SampleComponent.class));
        assertEquals("value", copy.getComponent(SampleComponent.class).getValue());
        assertTrue(source.hasComponent(SampleComponent.class));
        assertEquals("value", source.getComponent(SampleComponent.class).getValue());
    }

    @Test
    public void editComponentInteractingMultipleEntityRefs() {
        EntityRef source = simpleEntityManager.createEntity();

        EntityRef copy = simpleEntityManager.createNewEntityRef(source);
        SampleComponent component = copy.createComponent(SampleComponent.class);
        copy.saveChanges();

        SampleComponent sourceComponent = source.getComponent(SampleComponent.class);
        assertNotNull(sourceComponent);
        assertNull(sourceComponent.getValue());

        // Unsaved change is not visible in the source
        component.setValue("value");
        assertNull(sourceComponent.getValue());
        assertEquals("value", component.getValue());

        // Changes are immediately visible in the source after save
        copy.saveChanges();
        assertEquals("value", sourceComponent.getValue());
        assertEquals("value", component.getValue());
    }

    @Test
    public void removeComponentInteractingMultipleEntityRefs() {
        EntityRef source = simpleEntityManager.createEntity();

        EntityRef copy = simpleEntityManager.createNewEntityRef(source);
        SampleComponent component = copy.createComponent(SampleComponent.class);
        copy.saveChanges();

        assertTrue(source.hasComponent(SampleComponent.class));

        //noinspection unchecked
        copy.removeComponents(SampleComponent.class);
        copy.saveChanges();

        assertFalse(source.hasComponent(SampleComponent.class));
    }

    @Test
    public void destroyEntityMakesOtherEntityRefsNotExist() {
        EntityRef source = simpleEntityManager.createEntity();

        EntityRef copy = simpleEntityManager.createNewEntityRef(source);

        assertTrue(source.exists());
        assertTrue(copy.exists());

        simpleEntityManager.destroyEntity(copy);
        assertFalse(source.exists());
        assertFalse(copy.exists());
    }

    @Test
    public void createEntityDataWrapper() {
        Listener listener = new Listener();
        simpleEntityManager.addEntityEventListener(listener);

        ComponentData sampleComponentData = new ComponentData() {
            @Override
            public Class<? extends Component> getComponentClass() {
                return SampleComponent.class;
            }

            @Override
            public Map<String, Object> getFields() {
                return Collections.singletonMap("value", "a");
            }
        };

        EntityData data = new EntityData() {
            @Override
            public ComponentData getComponent(Class<? extends Component> componentClass) {
                if (componentClass == SampleComponent.class)
                    return sampleComponentData;
                return null;
            }

            @Override
            public Iterable<? extends ComponentData> getComponents() {
                return Collections.singleton(sampleComponentData);
            }
        };

        EntityRef result = simpleEntityManager.wrapEntityData(data);

        assertEquals(0, listener.events.size());

        assertEquals("a", result.getComponent(SampleComponent.class).getValue());
    }

    @Test
    public void notifyOnAddingComponent() {
        Listener listener = new Listener();
        simpleEntityManager.addEntityEventListener(listener);

        EntityRef entity = simpleEntityManager.createEntity();
        SampleComponent component = entity.createComponent(SampleComponent.class);

        assertEquals(0, listener.events.size());

        entity.saveChanges();

        assertEquals(1, listener.events.size());
        EntityAndEvent entityAndEvent = listener.events.get(0);

        simpleEntityManager.isSameEntity(entityAndEvent.entity, entity);
        assertTrue(entityAndEvent.event instanceof AfterComponentAdded);
    }

    @Test
    public void notifyOnUpdatingComponent() {
        EntityRef entity = simpleEntityManager.createEntity();
        SampleComponent component = entity.createComponent(SampleComponent.class);
        entity.saveChanges();

        Listener listener = new Listener();
        simpleEntityManager.addEntityEventListener(listener);

        component.setValue("value");

        assertEquals(0, listener.events.size());

        entity.saveChanges();

        assertEquals(1, listener.events.size());
        EntityAndEvent entityAndEvent = listener.events.get(0);

        simpleEntityManager.isSameEntity(entityAndEvent.entity, entity);
        assertTrue(entityAndEvent.event instanceof AfterComponentUpdated);

        AfterComponentUpdated event = (AfterComponentUpdated) entityAndEvent.event;
        assertTrue(event.getComponents().contains(SampleComponent.class));
        assertNull(event.getOldComponent(SampleComponent.class).getValue());
        assertEquals("value", event.getNewComponent(SampleComponent.class).getValue());
    }

    @Test
    public void notifyOnDestroyingEntity() {
        EntityRef entity = simpleEntityManager.createEntity();
        SampleComponent component = entity.createComponent(SampleComponent.class);
        entity.saveChanges();

        Listener listener = new Listener();
        simpleEntityManager.addEntityEventListener(listener);

        assertEquals(0, listener.events.size());

        simpleEntityManager.destroyEntity(entity);

        assertEquals(2, listener.events.size());
        EntityAndEvent entityAndEvent = listener.events.get(0);

        simpleEntityManager.isSameEntity(entityAndEvent.entity, entity);
        assertTrue(entityAndEvent.event instanceof BeforeComponentRemoved);

        entityAndEvent = listener.events.get(1);

        simpleEntityManager.isSameEntity(entityAndEvent.entity, entity);
        assertTrue(entityAndEvent.event instanceof AfterComponentRemoved);
    }

    @Test
    public void notifyOnRemovingComponent() {
        EntityRef entity = simpleEntityManager.createEntity();
        SampleComponent component = entity.createComponent(SampleComponent.class);
        entity.saveChanges();

        Listener listener = new Listener();
        simpleEntityManager.addEntityEventListener(listener);

        assertEquals(0, listener.events.size());

        entity.removeComponents(SampleComponent.class);
        entity.saveChanges();

        assertEquals(2, listener.events.size());
        EntityAndEvent entityAndEvent = listener.events.get(0);

        simpleEntityManager.isSameEntity(entityAndEvent.entity, entity);
        assertTrue(entityAndEvent.event instanceof BeforeComponentRemoved);

        entityAndEvent = listener.events.get(1);

        simpleEntityManager.isSameEntity(entityAndEvent.entity, entity);
        assertTrue(entityAndEvent.event instanceof AfterComponentRemoved);
    }

    private class Listener implements EntityEventListener {
        private List<EntityAndEvent> events = new LinkedList<>();

        @Override
        public void eventSent(EntityRef entity, Event event) {
            events.add(new EntityAndEvent(entity, event));
        }
    }

    @RegisterSystem(
            shared = InternalGameLoop.class)
    public static class MockInternalGameLoop implements InternalGameLoop {
        @Override
        public void addInternalGameLoopListener(InternalGameLoopListener internalGameLoopListener) {

        }

        @Override
        public void removeInternalGameLooplListener(InternalGameLoopListener internalGameLoopListener) {

        }

        @Override
        public void processUpdate() {

        }
    }

    private static class EntityAndEvent {
        public final EntityRef entity;
        public final Event event;

        public EntityAndEvent(EntityRef entity, Event event) {
            this.entity = entity;
            this.event = event;
        }
    }
}