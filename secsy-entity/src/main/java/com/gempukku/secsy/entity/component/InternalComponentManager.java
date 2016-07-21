package com.gempukku.secsy.entity.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;

import java.util.Map;

public interface InternalComponentManager {

    /**
     * Creates mutable component for specific source component. Throws an exception if this object already has a
     * Component of that class already.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T extends Component> T createComponent(EntityRef entity, Class<T> clazz);

    /**
     * Gets a component wrapper with the specified value object.
     *
     * @param originalComponent
     * @param <T>
     * @return
     */
    <T extends Component> T copyComponent(EntityRef entity, T originalComponent);

    <T extends Component> T copyComponentUnmodifiable(T originalComponent, boolean useOriginalReference);

    /**
     * Stores pending changes for the component wrapper with the specified value object.
     *
     * @param originalComponent
     * @param changedComponent
     * @param <T>
     */
    <T extends Component> void saveComponent(T originalComponent, T changedComponent);

    /**
     * Returns the class used to represent the component.
     *
     * @param component
     * @param <T>
     * @return
     */
    <T extends Component> Class<T> getComponentClass(T component);

    EntityRef getComponentEntity(Component component);

    /**
     * Returns all the fields in the component an their types.
     *
     * @param component
     * @return
     */
    Map<String, Class<?>> getComponentFieldTypes(Component component);

    /**
     * Returns the value of the field of this component.
     *
     * @param component
     * @param fieldName
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getComponentFieldValue(Component component, String fieldName, Class<T> clazz);

    void setComponentFieldValue(Component component, String fieldName, Object fieldValue);
}
