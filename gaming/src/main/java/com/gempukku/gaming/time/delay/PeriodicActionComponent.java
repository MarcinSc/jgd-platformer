package com.gempukku.gaming.time.delay;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface PeriodicActionComponent extends Component {
    Map<String, Long> getActionIdWakeUp();

    void setActionIdWakeUp(Map<String, Long> actionWakeUps);

    Map<String, Long> getActionIdPeriod();

    void setActionIdPeriod(Map<String, Long> actionPeriods);
}
