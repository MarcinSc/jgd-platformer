package com.gempukku.gaming.asset.reflections;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.context.util.Prioritable;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.List;

@RegisterSystem
public class ReflectionsSystem implements LifeCycleSystem, Prioritable {
    @Inject
    private EntityManager entityManager;

    @Override
    public float getPriority() {
        return 100;
    }

    @Override
    public void initialize() {
        EntityRef entity = entityManager.createEntity();
        GatherReflectionScanners scanners = new GatherReflectionScanners();
        entity.send(scanners);

        List<Scanner> scannersList = scanners.getScanners();
        scannersList.add(new SubTypesScanner());

        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(scannersList.toArray(new Scanner[scannersList.size()]))
                .setUrls(ClasspathHelper.forJavaClassPath());

        Reflections reflections = new Reflections(scanConfiguration);
        entity.send(new ReflectionsScanned(reflections));

        entityManager.destroyEntity(entity);
    }
}
