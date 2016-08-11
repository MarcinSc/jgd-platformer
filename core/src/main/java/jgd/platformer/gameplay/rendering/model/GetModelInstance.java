package jgd.platformer.gameplay.rendering.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.event.Event;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GetModelInstance extends Event {
    private Vector3 location;
    private float rotationY;
    private Vector3 scale;

    private List<ModelInstance> modelInstances = new LinkedList<>();

    public GetModelInstance(Vector3 location, float rotationY, Vector3 scale) {
        this.location = location;
        this.rotationY = rotationY;
        this.scale = scale;
    }

    public void appendModelInstance(ModelInstance modelInstance) {
        modelInstances.add(modelInstance);
    }

    public Vector3 getLocation() {
        return location;
    }

    public float getRotationY() {
        return rotationY;
    }

    public Vector3 getScale() {
        return scale;
    }

    public Collection<ModelInstance> getInstances() {
        return Collections.unmodifiableCollection(modelInstances);
    }
}
