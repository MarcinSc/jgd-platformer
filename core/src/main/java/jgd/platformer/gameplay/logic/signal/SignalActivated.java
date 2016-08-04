package jgd.platformer.gameplay.logic.signal;

import com.gempukku.secsy.entity.event.Event;

public class SignalActivated extends Event {
    private String channel;

    public SignalActivated(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
