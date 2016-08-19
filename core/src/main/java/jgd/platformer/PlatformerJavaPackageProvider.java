package jgd.platformer;

import com.gempukku.gaming.asset.JavaPackageProvider;
import com.gempukku.secsy.context.annotation.RegisterSystem;

import java.util.Arrays;
import java.util.Collection;

@RegisterSystem(
        shared = JavaPackageProvider.class
)
public class PlatformerJavaPackageProvider implements JavaPackageProvider {
    @Override
    public Collection<String> getJavaPackages() {
        return Arrays.asList("com.gempukku", "jgd.platformer");
    }
}
