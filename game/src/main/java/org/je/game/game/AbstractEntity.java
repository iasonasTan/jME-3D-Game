package org.je.game.game;

import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import java.util.List;

public abstract class AbstractEntity {
    protected final Context context;

    protected boolean jumping = false;
    protected float velocityVertical = Game.GRAVITY;
    protected boolean enableCollisions = true;

    protected AbstractEntity(Context context) {
        this.context = context;
    }

    public abstract Vector3f getLocation();
    public abstract void setLocation(Vector3f location);
    protected abstract BoundingVolume getBound();

    public void update(float tpf) {
        Vector3f locationWithGravity = getLocation().clone();
        locationWithGravity.y -= velocityVertical * tpf;
        if (setPos(locationWithGravity)) {
            velocityVertical += Game.GRAVITY * tpf;
        } else {
            velocityVertical = 0f;
            jumping = false;
        }
    }
    
    protected void jump() {
        if (jumping) return;
        velocityVertical = -7f;
        jumping = true;
    }

    public boolean move(Vector3f steps) {
        Vector3f newLocation = getLocation().clone().add(steps);
        return setPos(newLocation);
    }

    public boolean setPos(Vector3f newLocation) {
        Vector3f prevLoc = getLocation().clone();
        setLocation(newLocation);

        if (!enableCollisions) {
            setLocation(newLocation);
            return true;
        }
        
        if (context.hasCollisionWithMap(this)) {
            setLocation(prevLoc);
            return false;
        }

        return true;
    }
}
