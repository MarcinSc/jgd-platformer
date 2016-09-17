package com.gempukku.secsy.entity.component.map;

import com.gempukku.secsy.entity.SampleComponent;
import com.gempukku.secsy.entity.component.MapNamingConventionProxyComponentManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MapAnnotationDrivenProxyComponentManagerTest {
    private MapNamingConventionProxyComponentManager factory;

    @Before
    public void setup() throws NoSuchMethodException {
        factory = new MapNamingConventionProxyComponentManager();
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
}
