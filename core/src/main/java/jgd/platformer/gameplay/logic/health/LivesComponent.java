package jgd.platformer.gameplay.logic.health;

import com.gempukku.secsy.entity.Component;

public interface LivesComponent extends Component {
    int getLivesCount();

    void setLivesCount(int livesCount);
}
