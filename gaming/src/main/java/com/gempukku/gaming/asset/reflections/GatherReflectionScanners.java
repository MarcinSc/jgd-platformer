package com.gempukku.gaming.asset.reflections;

import com.gempukku.secsy.entity.event.Event;
import org.reflections.scanners.Scanner;

import java.util.LinkedList;
import java.util.List;

public class GatherReflectionScanners extends Event {
    private List<Scanner> scanners = new LinkedList<>();

    public void addScanner(Scanner scanner) {
        scanners.add(scanner);
    }

    public List<Scanner> getScanners() {
        return scanners;
    }
}
