package com.gempukku.secsy.entity;

import com.gempukku.secsy.entity.dispatch.ReceiveEvent;

import static org.junit.Assert.assertNotNull;

public class SampleSystem {
    public int validCalls;
    public int invalidCalls;

    @ReceiveEvent
    public void validMethod(SampleEvent event, EntityRef entity, SampleComponent sampleComponent) {
        assertNotNull(event);
        assertNotNull(entity);
        assertNotNull(sampleComponent);

        validCalls++;
    }

    @ReceiveEvent
    private void invalidMethodPrivate(SampleEvent event, EntityRef entity, SampleComponent sampleComponent) {
        invalidCalls++;
    }

    @ReceiveEvent
    public int invalidMethodReturnsInt(SampleEvent event, EntityRef entity, SampleComponent sampleComponent) {
        invalidCalls++;
        return 0;
    }

    @ReceiveEvent
    public void invalidMethodMissingEntity(SampleEvent event, SampleComponent sampleComponent) {
        invalidCalls++;
    }

    @ReceiveEvent
    public void invalidMethodMissingEvent(EntityRef entity, SampleComponent sampleComponent) {
        invalidCalls++;
    }

    @ReceiveEvent
    public void invalidMethodAdditionalParameter(SampleEvent event, EntityRef entity, Object object, SampleComponent sampleComponent) {
        invalidCalls++;
    }

    @ReceiveEvent
    public void throwingExceptionMethod(SampleEvent event, EntityRef entity, SampleComponent2 sampleComponent2) {
        validCalls++;
        throw new RuntimeException();
    }
}
