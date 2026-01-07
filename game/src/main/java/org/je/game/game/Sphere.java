package org.je.game.game;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.List;

public final class Sphere extends Entity {
    private final Spatial mModel;
    private final Vector3f mSpeed;
    private final Enemy mEnemy;

    public Sphere(Vector3f speed, AssetManager assetManager, Node rootNode, Entity parent, List<Spatial> map, Enemy mEnemy) {
        super(map);
        mModel = assetManager.loadModel("Models/Bullet.obj");
        this.mEnemy = mEnemy;
        rootNode.attachChild(mModel);
        setLocation(parent.getLocation());
        speed.multLocal(20f);
        mSpeed = speed;
    }

    @Override
    public Vector3f getLocation() {
        return mModel.getWorldTranslation();
    }

    @Override
    public void setLocation(Vector3f location) {
        mModel.setLocalTranslation(location);
    }

    @Override
    protected BoundingVolume getBound() {
        return mModel.getWorldBound();
    }

    public void update(float delta) {
        Vector3f vec = mSpeed.clone();
        vec.x *= delta;
        vec.y *= delta;
        vec.z *= delta;
        mModel.move(vec);

        if(getBound().intersects(mEnemy.getBound())) {
            Player.increaseScore();
        }
    }

}
