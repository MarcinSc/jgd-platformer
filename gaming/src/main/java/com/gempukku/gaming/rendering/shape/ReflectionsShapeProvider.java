package com.gempukku.gaming.rendering.shape;

import com.gempukku.gaming.asset.reflections.GatherReflectionScanners;
import com.gempukku.gaming.asset.reflections.ReflectionsScanned;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.google.common.collect.Multimap;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.vfs.Vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "shapeProvider",
        shared = ShapeProvider.class)
public class ReflectionsShapeProvider implements ShapeProvider {
    private Map<String, ShapeDef> shapesById;

    @ReceiveEvent
    public void createScanner(GatherReflectionScanners event, EntityRef entityRef) {
        event.addScanner(new ShapeScanner());
    }

    @ReceiveEvent
    public void readShapes(ReflectionsScanned event, EntityRef entityRef) {
        shapesById = new HashMap<>();

        Multimap<String, String> resources = event.getReflections().getStore().get(ShapeScanner.class);

        ObjectMapper objectMapper = new ObjectMapper();

        for (String shapeId : resources.keySet()) {
            Collection<String> paths = resources.get(shapeId);
            if (paths.size() > 1)
                throw new IllegalStateException("More than one shape with the same name found: " + shapeId);

            try {
                InputStream shapeDefInputStream = ReflectionsShapeProvider.class.getResourceAsStream("/" + paths.iterator().next());
                try {
                    ShapeDef shapeDef = objectMapper.readValue(shapeDefInputStream, ShapeDef.class);
                    shapesById.put(shapeId, shapeDef);
                } finally {
                    shapeDefInputStream.close();
                }
            } catch (IOException exp) {
                throw new RuntimeException("Unable to read shape data", exp);
            }
        }
    }

    @Override
    public ShapeDef getShapeById(String shapeId) {
        return shapesById.get(shapeId);
    }

    private static class ShapeScanner extends ResourcesScanner {
        private String extension = ".shape";
        private int extensionLength = extension.length();

        public boolean acceptsInput(String file) {
            return file.endsWith(extension);
        }

        public Object scan(Vfs.File file, Object classObject) {
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.length() - extensionLength);
            this.getStore().put(fileName, file.getRelativePath());
            return classObject;
        }
    }
}
