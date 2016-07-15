package jgd.platformer.menu;

import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface SplashSeriesComponent extends Component {
    String getTextureAtlasId();

    List<String> getSplashDescriptions();
}
