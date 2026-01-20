package org.je.game.game;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.List;

public final class Sphere extends AbstractEntity {
    private final Spatial mModel;
    private final Vector3f mSpeed;
    private final Enemy mEnemy;

    public Sphere(Context context, Vector3f speed, AbstractEntity parent, Enemy enemy) {
        super(context);
        mEnemy = enemy;
        mModel = context.assetManager().loadModel("Models/Bullet.obj");
        mSpeed = speed.mult(20f);
        context.rootNode().attachChild(mModel);
        setLocation(parent.getLocation().clone());
    }

    @Override
    public void update(float tpf) {
        Vector3f vec = mSpeed.clone();
        vec.x *= tpf;
        vec.y *= tpf;
        vec.z *= tpf;
        mModel.move(vec);

        if (getBound().intersects(mEnemy.getBound())) {
            Player.increaseScore();
        }
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
}
