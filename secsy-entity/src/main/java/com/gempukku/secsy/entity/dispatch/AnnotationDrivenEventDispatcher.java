package com.gempukku.secsy.entity.dispatch;

import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.ContextAwareSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.context.util.Prioritable;
import com.gempukku.secsy.context.util.PriorityCollection;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityEventListener;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.InternalEntityManager;
import com.gempukku.secsy.entity.event.ComponentEvent;
import com.gempukku.secsy.entity.event.ConsumableEvent;
import com.gempukku.secsy.entity.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "annotationEventDispatcher"
)
public class AnnotationDrivenEventDispatcher implements ContextAwareSystem<Object>, LifeCycleSystem, EntityEventListener {
    @Inject
    private InternalEntityManager internalEntityManager;
    @Inject(optional = true)
    private PriorityResolver priorityResolver;

    private Map<Class<? extends Event>, PriorityCollection<EventListenerDefinition>> eventListenerDefinitions = new HashMap<>();
    private Iterable<Object> systems;

    @Override
    public void setContext(SystemContext<Object> context) {
        systems = context.getSystems();
    }

    @Override
    public void initialize() {
        internalEntityManager.addEntityEventListener(this);
    }

    @Override
    public void postInitialize() {
        for (Object system : systems) {
            scanSystem(system);
        }
    }

    private void scanSystem(Object system) {
        for (Method method : system.getClass().getDeclaredMethods()) {
            final ReceiveEvent receiveEventAnnotation = method.getAnnotation(ReceiveEvent.class);
            if (receiveEventAnnotation != null) {
                if (method.getReturnType().equals(Void.TYPE)
                        && Modifier.isPublic(method.getModifiers())) {
                    final Class<?>[] parameters = method.getParameterTypes();
                    if (parameters.length >= 2) {
                        if (Event.class.isAssignableFrom(parameters[0])
                                && EntityRef.class.isAssignableFrom(parameters[1])) {
                            boolean valid = true;
                            for (int i = 2; i < parameters.length; i++) {
                                if (!Component.class.isAssignableFrom(parameters[i])) {
                                    valid = false;
                                    break;
                                }
                            }

                            if (valid) {
                                Class<? extends Component>[] components = new Class[parameters.length - 2];
                                for (int i = 2; i < parameters.length; i++) {
                                    components[i - 2] = (Class<? extends Component>) parameters[i];
                                }

                                addListenerDefinition((Class<? extends Event>) parameters[0],
                                        new EventListenerDefinition(system, method, components, getPriority(receiveEventAnnotation)));
                            }
                        }
                    }
                }
            }
        }
    }

    private float getPriority(ReceiveEvent receiveEventAnnotation) {
        if (priorityResolver != null && !receiveEventAnnotation.priorityName().equals("")) {
            Float priority = priorityResolver.getPriority(receiveEventAnnotation.priorityName());
            if (priority != null)
                return priority;
        }
        return receiveEventAnnotation.priority();
    }

    private void addListenerDefinition(Class<? extends Event> clazz, EventListenerDefinition eventListenerDefinition) {
        PriorityCollection<EventListenerDefinition> eventListenerDefinitions = this.eventListenerDefinitions.get(clazz);
        if (eventListenerDefinitions == null) {
            eventListenerDefinitions = new PriorityCollection<>();
            this.eventListenerDefinitions.put(clazz, eventListenerDefinitions);
        }
        eventListenerDefinitions.add(eventListenerDefinition);
    }

    @Override
    public void eventSent(EntityRef entity, Event event) {
        ConsumableEvent consumableEvent = null;
        if (event instanceof ConsumableEvent)
            consumableEvent = (ConsumableEvent) event;
        PriorityCollection<EventListenerDefinition> eventListenerDefinitions = this.eventListenerDefinitions.get(event.getClass());
        if (eventListenerDefinitions != null) {
            for (EventListenerDefinition eventListenerDefinition : eventListenerDefinitions) {
                boolean valid = true;
                for (Class<? extends Component> componentRequired : eventListenerDefinition.getComponentParameters()) {
                    if (!entity.hasComponent(componentRequired)) {
                        valid = false;
                        break;
                    }
                }
                if (valid && event instanceof ComponentEvent) {
                    // Either defined components by listener have to be empty (interested in receiving all changes),
                    // or at least one of the components that is defined by listener has to be in the ComponentEvent collection
                    if (eventListenerDefinition.getComponentParameters().length != 0) {
                        Collection<Class<? extends Component>> eventComponents = ((ComponentEvent) event).getComponents();
                        boolean hasAtLeastOne = false;
                        for (Class<? extends Component> definedComponent : eventListenerDefinition.getComponentParameters()) {
                            if (eventComponents.contains(definedComponent)) {
                                hasAtLeastOne = true;
                                break;
                            }
                        }
                        if (!hasAtLeastOne) {
                            valid = false;
                        }
                    }
                }
                if (valid) {
                    eventListenerDefinition.eventReceived(entity, event);
                    if (consumableEvent != null && consumableEvent.isConsumed())
                        break;
                }
            }
        }
    }

    private class EventListenerDefinition implements Prioritable {
        private Object system;
        private Method method;
        private Class<? extends Component>[] componentParameters;
        private float priority;

        private EventListenerDefinition(Object system, Method method, Class<? extends Component>[] componentParameters, float priority) {
            this.system = system;
            this.method = method;
            this.componentParameters = componentParameters;
            this.priority = priority;
        }

        @Override
        public float getPriority() {
            return priority;
        }

        public Class<? extends Component>[] getComponentParameters() {
            return componentParameters;
        }

        public void eventReceived(EntityRef entity, Event event) {
            Object[] params = new Object[2 + componentParameters.length];
            params[0] = event;
            params[1] = entity;
            int index = 2;
            for (Class<? extends Component> componentParameter : componentParameters) {
                params[index++] = entity.getComponent(componentParameter);
            }

            try {
                method.invoke(system, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
