package org.je.game.game;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.List;

public final class Enemy extends Entity {
    private final Spatial mModel;
    private final Player mPlayer;

    public Enemy(AssetManager am, List<Spatial> map, Player player, Node rootNode) {
        super(map);
        mModel = am.loadModel("Models/Enemy.obj");
        mPlayer = player;
        rootNode.attachChild(mModel);

        setLocation(new Vector3f(0, 400, 0));
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        float speed = 10f;

        Vector3f playerPos = mPlayer.getLocation().clone();
        float diffX = playerPos.x - getLocation().x;
        float diffZ = playerPos.z - getLocation().z;
        float diff = (float)Math.sqrt(diffX*diffX + diffZ*diffZ);

        float mVelocityX = diffX/diff;
        float mVelocityZ = diffZ/diff;
        if (!move(new Vector3f(mVelocityX, 0, mVelocityZ).mult(tpf).mult(speed))) {
            velocityVertical = -10f;
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
