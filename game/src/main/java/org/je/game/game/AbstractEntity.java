package org.je.game.game;

import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import java.util.List;

public abstract class AbstractEntity {
    protected final Context context;

    protected boolean jumping = false, falling = false;
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

        boolean moved = setPos(locationWithGravity);
        if (moved) {
            velocityVertical += Game.GRAVITY * tpf;
            if(!falling) {
                falling = true;
                jumping = false;
                onFall(tpf);
            }
        } else {
            if(falling) {
                velocityVertical = 0f;
                falling = false;
                onStopFalling(tpf);
            }
        }
    }
    
    protected void jump() {
        if (jumping)
            return;
        velocityVertical = -7f;
        jumping = true;
    }

    public boolean move(Vector3f steps) {
        Vector3f newLocation = getLocation().clone().add(steps);
        return setPos(newLocation);
    }

    public boolean setPos(Vector3f newLocation) {
        if (!enableCollisions) {
            setLocation(newLocation);
            return true;
        }
        
        Vector3f prevLoc = getLocation().clone();
        setLocation(newLocation);

        if (context.hasCollisionWithMap(this)) {
            setLocation(prevLoc);
            return false;
        } else { 
            return true;
        }
    }

    protected void onFall(float tpf) {
    }

    protected void onStopFalling(float tpf) {
    }
}
