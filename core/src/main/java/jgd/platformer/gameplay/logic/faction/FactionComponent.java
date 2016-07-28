package jgd.platformer.gameplay.logic.faction;

import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface FactionComponent extends Component {
    List<String> getEnemies();

    String getName();
}
