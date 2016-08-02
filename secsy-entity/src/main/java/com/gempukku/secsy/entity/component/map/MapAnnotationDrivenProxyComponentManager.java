package com.gempukku.secsy.entity.component.map;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.component.ComponentManager;
import com.gempukku.secsy.entity.component.InternalComponentManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem(profiles = {"annotationComponents"}, shared = {ComponentManager.class, InternalComponentManager.class})
public class MapAnnotationDrivenProxyComponentManager implements ComponentManager, InternalComponentManager {
    private static final Object NULL_VALUE = new Object();
    private Map<Class<? extends Component>, ComponentDef> componentDefinitions = new HashMap<>();

    @Override
    public <T extends Component> T createComponent(EntityRef entity, Class<T> clazz) {
        if (componentDefinitions.get(clazz) == null) {
            componentDefinitions.put(clazz, new ComponentDef(clazz));
        }
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new ComponentView(entity, clazz, new HashMap<>(), false));
    }

    @Override
    public <T extends Component> T copyComponent(EntityRef entity, T originalComponent) {
        return copyComponentInternal(entity, originalComponent, false, true);
    }

    @Override
    public <T extends Component> T copyComponentUnmodifiable(T originalComponent, boolean useOriginalReference) {
        return copyComponentInternal(null, originalComponent, true, useOriginalReference);
    }

    private <T extends Component> T copyComponentInternal(EntityRef entity, T originalComponent,
                                                          boolean readOnly, boolean useOriginalReference) {
        final ComponentView componentView = extractComponentView(originalComponent);

        Map<String, Object> values = componentView.storedValues;
        if (!useOriginalReference)
            values = new HashMap<>(values);

        //noinspection unchecked
        return (T) Proxy.newProxyInstance(componentView.clazz.getClassLoader(), new Class[]{componentView.clazz},
                new ComponentView(entity, componentView.clazz, values, readOnly));
    }

    @Override
    public <T extends Component> void saveComponent(T originalComponent, T changedComponent) {
        ComponentView destination = extractComponentView(originalComponent);
        ComponentView source = extractComponentView(changedComponent);
        for (Map.Entry<String, Object> changeEntry : source.changes.entrySet()) {
            String fieldName = changeEntry.getKey();
            Object fieldValue = changeEntry.getValue();

            if (fieldValue == NULL_VALUE)
                destination.storedValues.remove(fieldName);
            else
                destination.storedValues.put(fieldName, fieldValue);
        }

        source.changes.clear();
    }

    @Override
    public void invalidateComponent(Component component) {
        extractComponentView(component).invalidate();
    }

    @Override
    public boolean hasSameValues(Component component1, Component component2) {
        ComponentView componentView1 = extractComponentView(component1);
        ComponentView componentView2 = extractComponentView(component2);
        return componentView1.clazz == componentView2.clazz
                && createConsolidatedFieldMap(componentView1).
                equals(createConsolidatedFieldMap(componentView2));
    }

    @Override
    public EntityRef getComponentEntity(Component component) {
        return extractComponentView(component).entity;
    }

    @Override
    public <T extends Component> Class<T> getComponentClass(T component) {
        //noinspection unchecked
        return (Class<T>) extractComponentView(component).clazz;
    }

    @Override
    public Map<String, Class<?>> getComponentFieldTypes(Component component) {
        Class<Component> clazz = getComponentClass(component);
        return Collections.unmodifiableMap(componentDefinitions.get(clazz).getFieldTypes());
    }

    @Override
    public <T> T getComponentFieldValue(Component component, String fieldName, Class<T> clazz) {
        //noinspection unchecked
        return (T) extractComponentView(component).storedValues.get(fieldName);
    }

    @Override
    public void setComponentFieldValue(Component component, String fieldName, Object fieldValue) {
        extractComponentView(component).storedValues.put(fieldName, fieldValue);
    }

    private Map<String, Object> createConsolidatedFieldMap(ComponentView componentView) {
        Map<String, Object> values = new HashMap<>();

        values.putAll(componentView.storedValues);

        for (Map.Entry<String, Object> changedEntry : componentView.changes.entrySet()) {
            if (changedEntry.getValue() == NULL_VALUE)
                values.remove(changedEntry.getKey());
            else
                values.put(changedEntry.getKey(), changedEntry.getValue());
        }
        return values;
    }

    private <T extends Component> ComponentView extractComponentView(T originalComponent) {
        return (ComponentView) Proxy.getInvocationHandler(originalComponent);
    }

    private class ComponentDef {
        private Map<String, Class<?>> fieldTypes = new HashMap<>();

        private ComponentDef(Class<? extends Component> clazz) {
            for (Method method : clazz.getDeclaredMethods()) {
                final GetProperty get = method.getAnnotation(GetProperty.class);
                if (get != null) {
                    final String fieldName = get.value();
                    final Class<?> fieldType = method.getReturnType();

                    final Class<?> existingType = fieldTypes.get(fieldName);
                    if (existingType != null) {
                        if (existingType != fieldType) {
                            throw new IllegalStateException("Invalid component definition, field " + fieldName + " uses different value types");
                        }
                    } else {
                        fieldTypes.put(fieldName, fieldType);
                    }
                }

                final SetProperty set = method.getAnnotation(SetProperty.class);
                if (set != null) {
                    final String fieldName = set.value();
                    final Class<?> fieldType = method.getParameterTypes()[0];

                    final Class<?> existingType = fieldTypes.get(fieldName);
                    if (existingType != null) {
                        if (existingType != fieldType) {
                            throw new IllegalStateException("Invalid component definition, field " + fieldName + " uses different value types");
                        }
                    } else {
                        fieldTypes.put(fieldName, fieldType);
                    }
                }
            }
        }

        private Map<String, Class<?>> getFieldTypes() {
            return fieldTypes;
        }
    }

    private class ComponentView implements InvocationHandler {
        private EntityRef entity;
        private Class<? extends Component> clazz;
        private Map<String, Object> storedValues;
        private Map<String, Object> changes = new HashMap<>();
        private boolean readOnly;
        private boolean invalid;

        private ComponentView(EntityRef entity, Class<? extends Component> clazz, Map<String, Object> storedValues,
                              boolean readOnly) {
            this.entity = entity;
            this.clazz = clazz;
            this.storedValues = storedValues;
            this.readOnly = readOnly;
        }

        public void invalidate() {
            invalid = true;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (invalid)
                throw new IllegalStateException("Attempted to invoke a method on a Component that had it's state saved");

            final GetProperty get = method.getAnnotation(GetProperty.class);
            if (get != null) {
                return handleGet(get.value());
            }
            final SetProperty set = method.getAnnotation(SetProperty.class);
            if (set != null) {
                return handleSet(args[0], set.value());
            }
            throw new UnsupportedOperationException("Component method invoked without property defined");
        }

        private Object handleSet(Object arg, String fieldName) {
            if (readOnly)
                throw new UnsupportedOperationException("This is a read only component");
            if (arg == null) {
                arg = NULL_VALUE;
            }
            changes.put(fieldName, arg);

            return null;
        }

        private Object handleGet(String fieldName) {
            final Object changedValue = changes.get(fieldName);
            if (changedValue != null) {
                if (changedValue == NULL_VALUE) {
                    return null;
                } else {
                    return changedValue;
                }
            } else {
                return storedValues.get(fieldName);
            }
        }
    }
}
