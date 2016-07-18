package jgd.platformer.menu;

import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface LevelSequenceComponent extends Component {
    List<String> getLevelNames();
}
