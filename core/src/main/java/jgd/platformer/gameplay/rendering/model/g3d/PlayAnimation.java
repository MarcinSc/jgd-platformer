package jgd.platformer.gameplay.rendering.model.g3d;

import com.gempukku.secsy.entity.event.Event;

public class PlayAnimation extends Event {
    private String animationName;
    private float speedMultiplier;
    private float transitionTime;
    private int loopCount;

    public PlayAnimation(String animationName, float speedMultiplier, float transitionTime, int loopCount) {
        this.animationName = animationName;
        this.speedMultiplier = speedMultiplier;
        this.transitionTime = transitionTime;
        this.loopCount = loopCount;
    }

    public String getAnimationName() {
        return animationName;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getTransitionTime() {
        return transitionTime;
    }

    public int getLoopCount() {
        return loopCount;
    }
}

