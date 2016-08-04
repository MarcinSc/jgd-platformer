package jgd.platformer.gameplay.logic.signal;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;

@RegisterSystem(
        profiles = "gameplay",
        shared = SignalManager.class
)
public class SignalSystem implements SignalManager, LifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    private EntityIndex signalConsumers;
    private EntityIndex signalProducers;

    @Override
    public void initialize() {
        signalConsumers = entityIndexManager.addIndexOnComponents(SignalConsumerComponent.class);
        signalProducers = entityIndexManager.addIndexOnComponents(SignalProducerComponent.class);
    }

    @Override
    public void signalActivated(EntityRef entityRef) {
        SignalProducerComponent producer = entityRef.getComponent(SignalProducerComponent.class);
        if (producer != null) {
            if (!producer.isProducingSignal()) {
                String channel = producer.getChannel();

                producer.setProducingSignal(true);
                entityRef.saveChanges();

                signalConsumers.forEach(
                        signalConsumer -> {
                            SignalConsumerComponent consumer = signalConsumer.getComponent(SignalConsumerComponent.class);
                            if (consumer.getChannel().equals(channel) && !consumer.isReceivingSignal()) {
                                consumer.setReceivingSignal(true);
                                signalConsumer.saveChanges();

                                signalConsumer.send(new SignalActivated(channel));
                            }
                        });
            }
        }
    }

    @Override
    public void signalDeactivated(EntityRef entityRef) {
        SignalProducerComponent producer = entityRef.getComponent(SignalProducerComponent.class);
        if (producer != null) {
            if (producer.isProducingSignal()) {
                String channel = producer.getChannel();

                producer.setProducingSignal(false);
                entityRef.saveChanges();

                if (!isChannelProducing(channel)) {
                    signalConsumers.forEach(
                            signalConsumer -> {
                                SignalConsumerComponent consumer = signalConsumer.getComponent(SignalConsumerComponent.class);
                                if (consumer.getChannel().equals(channel) && consumer.isReceivingSignal()) {
                                    consumer.setReceivingSignal(false);
                                    signalConsumer.saveChanges();

                                    signalConsumer.send(new SignalDeactivated(channel));
                                }
                            });
                }
            }
        }
    }

    private boolean isChannelProducing(String channel) {
        for (EntityRef signalProducerEntity : signalProducers) {
            SignalProducerComponent signalProducer = signalProducerEntity.getComponent(SignalProducerComponent.class);
            if (signalProducer.getChannel().equals(channel) && signalProducer.isProducingSignal())
                return true;
        }
        return false;
    }
}
