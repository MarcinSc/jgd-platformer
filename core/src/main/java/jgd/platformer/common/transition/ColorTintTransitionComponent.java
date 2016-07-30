package jgd.platformer.common.transition;

import com.gempukku.secsy.entity.Component;

public interface ColorTintTransitionComponent extends Component {
    long getStartTime();

    void setStartTime(long startTime);

    long getLength();

    void setLength(long length);

    float getFactorFrom();

    void setFactorFrom(float factorFrom);

    float getFactorTo();

    void setFactorTo(float factorTo);
}
