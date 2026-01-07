package org.je.game.game;

import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import java.util.List;

public abstract class Entity {
    private final List<Spatial> mMap;
    protected float velocityVertical = Game.GRAVITY;
    protected boolean enableCollisions = true;

    protected Entity(List<Spatial> mMap) {
        this.mMap = mMap;
    }

    public abstract Vector3f getLocation();
    public abstract void setLocation(Vector3f location);
    protected abstract BoundingVolume getBound();

    public void update(float tpf) {
        Vector3f camLocationWithGravity = getLocation().clone();
        camLocationWithGravity.y -= velocityVertical * tpf;
        if(setPos(camLocationWithGravity))
            velocityVertical +=Game.GRAVITY*tpf;
    }

    public boolean move(Vector3f steps) {
        Vector3f newLocation = getLocation().clone().add(steps);
        return setPos(newLocation);
    }

    public boolean setPos(Vector3f newLocation) {
        if(!enableCollisions) {
            setLocation(newLocation);
            return true;
        }
        boolean collides = false;
        float size = 2f;
        BoundingVolume cameraVolume = new BoundingSphere(size, newLocation);
        for (Spatial spatial : mMap) {
            BoundingVolume volume = spatial.getWorldBound().clone();
            if (volume.intersects(cameraVolume)) {
                collides = true;
            }
        }
        if(!collides) {
            setLocation(newLocation);
            return true;
        }
        return false;
    }
}
