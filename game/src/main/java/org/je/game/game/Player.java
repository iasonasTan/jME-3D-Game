package org.je.game.game;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
    private static int sScore = 0;

    public static void increaseScore() {
        sScore++;
        Game.sScoreText.setText("Score: "+sScore);
    }

    private static final String MOVE_FORWARD  = "MoveForward";
    private static final String MOVE_BACKWARD = "MoveBackward";
    private static final String MOVE_LEFT     = "MoveLeft";
    private static final String MOVE_RIGHT    = "MoveRight";

    private static final String MOVE_CAMERA_RIGHT = "MoveCameraRight";
    private static final String MOVE_CAMERA_LEFT  = "MoveCameraLeft";
    private static final String MOVE_CAMERA_UP    = "MoveCameraUp";
    private static final String MOVE_CAMERA_DOWN  = "MoveCameraDown";

    private static final String SHOOT = "PlayerShoot";
    private static final String JUMP  = "Jump";

    private final AssetManager mAssetManager;
    private final Node mRootNode;
    private final Camera mCam;
    private final List<Sphere> mSpheres = new ArrayList<>();
    private final List<Spatial> mMap;
    private Enemy mEnemy;

    private final float mSpeed = 15f;
    private float yaw = 0f;
    private float pitch = 0f;

    public Player(InputManager inputManager, Camera cam, List<Spatial> map, AssetManager assetManager, Node rootNode) {
        super(map);
        mCam = cam;
        mCam.setFov(100f);
        mAssetManager = assetManager;
        mRootNode = rootNode;
        mMap = map;

        addAttackListeners(inputManager);
        addMovementInputListeners(inputManager);
        addCameraInputListeners(inputManager);
    }

    public void setEnemy(Enemy enemy) {
        mEnemy = enemy;
    }

    private void addAttackListeners(InputManager inputManager) {
        inputManager.addMapping(SHOOT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        inputManager.addListener(new ActionListener(){
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if(isPressed) {
                    mSpheres.add(constructSphere());
                }
            }
            private Sphere constructSphere() {
                return new Sphere(mCam.getDirection(), mAssetManager, mRootNode, Player.this, mMap, mEnemy);
            }
        }, SHOOT);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        mSpheres.forEach(s -> s.update(tpf));
    }

    private void addMovementInputListeners(InputManager inputManager) {
        inputManager.addMapping(MOVE_FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(MOVE_LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(MOVE_RIGHT, new KeyTrigger(KeyInput.KEY_D));

        inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            switch(name) {
                case MOVE_FORWARD:
                    move(mCam.getDirection().clone()
                            .setY(0f).mult(tpf*mSpeed));
                    break;
                case MOVE_BACKWARD:
                    move(mCam.getDirection().clone()
                            .setY(0f).mult(tpf*-mSpeed));
                    break;
                case MOVE_LEFT:
                    move(mCam.getLeft().clone()
                            .setY(0f).mult(tpf*mSpeed));
                    break;
                case MOVE_RIGHT:
                    move(mCam.getLeft().clone()
                            .setY(0f).mult(tpf*-mSpeed));
                    break;
            }
        }, MOVE_FORWARD, MOVE_BACKWARD, MOVE_RIGHT, MOVE_LEFT);
    }

    private void addCameraInputListeners(InputManager inputManager) {
        inputManager.addMapping(MOVE_CAMERA_UP, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(MOVE_CAMERA_DOWN, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(MOVE_CAMERA_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(MOVE_CAMERA_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));

        inputManager.addMapping(MOVE_CAMERA_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(MOVE_CAMERA_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(MOVE_CAMERA_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(MOVE_CAMERA_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            final float SPEED = 0.4f;
            switch(name) {
                case MOVE_CAMERA_RIGHT: horizontalRotation(-SPEED, tpf); break;
                case MOVE_CAMERA_LEFT:  horizontalRotation(SPEED, tpf); break;
                case MOVE_CAMERA_UP:    verticalRotation(-SPEED/2, tpf); break;
                case MOVE_CAMERA_DOWN:  verticalRotation(SPEED/2, tpf); break;
            }
        }, MOVE_CAMERA_RIGHT, MOVE_CAMERA_LEFT, MOVE_CAMERA_UP, MOVE_CAMERA_DOWN);

        inputManager.addListener((ActionListener)(name, pressed, tpf) -> {
            if(JUMP.equals(name)) {
                velocityVertical = -5f;
            }
        }, JUMP);
    }

    public void horizontalRotation(float amount, float tpf) {
        yaw += amount * tpf;
        updateCamera();
    }

    public void verticalRotation(float amount, float tpf) {
        pitch -= amount * tpf;
        pitch = FastMath.clamp(
                pitch,
                -FastMath.HALF_PI + 0.01f,
                FastMath.HALF_PI - 0.01f
        );
        updateCamera();
    }

    private void updateCamera() {
        float x = FastMath.cos(pitch) * FastMath.sin(yaw);
        float z = FastMath.cos(pitch) * FastMath.cos(yaw);
        Vector3f dir = new Vector3f(x, FastMath.sin(pitch), z);

        mCam.lookAtDirection(dir.normalizeLocal(), Vector3f.UNIT_Y);
    }

    @Override
    public Vector3f getLocation() {
        return mCam.getLocation();
    }

    @Override
    public void setLocation(Vector3f location) {
        mCam.setLocation(location);
    }

    @Override
    protected BoundingVolume getBound() {
        return new BoundingSphere(2f, getLocation());
    }
}
