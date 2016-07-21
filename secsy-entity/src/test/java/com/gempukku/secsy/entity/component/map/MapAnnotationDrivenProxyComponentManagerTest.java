package com.gempukku.secsy.entity.component.map;

import com.gempukku.secsy.entity.SampleComponent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapAnnotationDrivenProxyComponentManagerTest {
    private MapAnnotationDrivenProxyComponentManager factory;

    @Before
    public void setup() throws NoSuchMethodException {
        factory = new MapAnnotationDrivenProxyComponentManager();
    }

    @Test
    public void testGetComponentClass() {
        final SampleComponent component = factory.createComponent(null, SampleComponent.class);
        assertEquals(SampleComponent.class, factory.getComponentClass(component));
    }

    @Test
    public void storeValueInPermanentStorage() {
        final SampleComponent component = factory.createComponent(null, SampleComponent.class);
        component.setValue("value");
        assertEquals("value", component.getValue());
        factory.saveComponent(component, component);
        assertEquals("value", factory.getComponentFieldValue(component, "value", String.class));
    }

    @Test
    public void setNullValue() {
        final SampleComponent component = factory.createComponent(null, SampleComponent.class);
        component.setValue("value");
        factory.saveComponent(component, component);

        component.setValue(null);
        assertNull(component.getValue());
        factory.saveComponent(component, component);
        assertNull(factory.getComponentFieldValue(component, "value", String.class));
    }

    @Test
    public void callingUndefinedMethod() {
        final SampleComponent component = factory.createComponent(null, SampleComponent.class);
        try {
            component.undefinedMethod();
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException exp) {
            // Expected
        }
    }
}
