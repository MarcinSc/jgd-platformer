package jgd.platformer.gameplay.logic.score;

import com.gempukku.secsy.entity.Component;

public interface ScoreComponent extends Component {
    int getScore();

    void setScore(int score);
}
