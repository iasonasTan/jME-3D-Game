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

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class Player extends AbstractEntity {
    private static int sScore = 0;

    public static void increaseScore() {
        sScore++;
        Game.sScoreText.setText("Score: "+sScore);
    }

    private final Camera mCam;
    private final List<Sphere> mSpheres = new ArrayList<>();
    private final Enemy mEnemy;

    private final float mSpeed = 18f;
    private float yaw = 0f;
    private float pitch = 0f;

    public Player(Context context, Camera cam, Enemy enemy) {
        super(context);
        mEnemy = enemy;
        mCam = cam;

        mCam.setFov(100f);
        setLocation(new Vector3f(-10, 50, -10));

        var im = context.inputManager();
        new AttackListener(im).apply();
        new MoveListener(im).apply();
        new CameraMoveListener(im).apply();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        mSpheres.forEach(s -> s.update(tpf));
        if (context.hasCollisionWithEntity(this)) {
            context.gameOver();
        }
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

    private abstract class AbstractListener implements InputListener {
        private final InputManager inputManager;

        public AbstractListener(InputManager im) {
            inputManager = im;
        }

        public abstract void apply();

        protected final void applyWith(String... actions) {
            inputManager.addListener(this, actions);
        }
    }

    private final class MoveListener extends AbstractListener implements AnalogListener {
        private static final String MOVE_FORWARD  = "MoveForward";
        private static final String MOVE_BACKWARD = "MoveBackward";
        private static final String MOVE_LEFT     = "MoveLeft";
        private static final String MOVE_RIGHT    = "MoveRight";
        private static final String JUMP          = "Jump";
    
        public MoveListener(InputManager im) {
            super(im);
            im.addMapping(MOVE_FORWARD,  new KeyTrigger(KeyInput.KEY_W));
            im.addMapping(MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
            im.addMapping(MOVE_LEFT,     new KeyTrigger(KeyInput.KEY_A));
            im.addMapping(MOVE_RIGHT,    new KeyTrigger(KeyInput.KEY_D));
            im.addMapping(JUMP,      new KeyTrigger(KeyInput.KEY_SPACE));
        }

        @Override
        public void 
        apply() {
            applyWith(MOVE_FORWARD, MOVE_BACKWARD, MOVE_RIGHT, MOVE_LEFT, JUMP);
        }

        @Override
        public void onAnalog(String name, float value, float tpf) {
            switch (name) {
                case MOVE_FORWARD:
                    move(mCam.getDirection().clone()
                            .setY(0f).mult(tpf * mSpeed));
                    break;
                case MOVE_BACKWARD:
                    move(mCam.getDirection().clone()
                            .setY(0f).mult(tpf * -mSpeed));
                    break;
                case MOVE_LEFT:
                    move(mCam.getLeft().clone()
                            .setY(0f).mult(tpf * mSpeed));
                    break;
                case MOVE_RIGHT:
                    move(mCam.getLeft().clone()
                            .setY(0f).mult(tpf * -mSpeed));
                    break;
                case JUMP:
                    jump();
                    break;
            }
        }
    }
    
    private final class CameraMoveListener extends AbstractListener implements AnalogListener {
        private static final String MOVE_CAMERA_RIGHT = "MoveCameraRight";
        private static final String MOVE_CAMERA_LEFT  = "MoveCameraLeft";
        private static final String MOVE_CAMERA_UP    = "MoveCameraUp";
        private static final String MOVE_CAMERA_DOWN  = "MoveCameraDown";
        
        public CameraMoveListener(InputManager im) {
            super(im);
            im.addMapping(MOVE_CAMERA_UP   , new KeyTrigger(KeyInput.KEY_UP   ));
            im.addMapping(MOVE_CAMERA_DOWN , new KeyTrigger(KeyInput.KEY_DOWN ));
            im.addMapping(MOVE_CAMERA_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));
            im.addMapping(MOVE_CAMERA_LEFT , new KeyTrigger(KeyInput.KEY_LEFT ));

            im.addMapping(MOVE_CAMERA_UP,    new MouseAxisTrigger(MouseInput.AXIS_Y, true ));
            im.addMapping(MOVE_CAMERA_DOWN,  new MouseAxisTrigger(MouseInput.AXIS_Y, false));
            im.addMapping(MOVE_CAMERA_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
            im.addMapping(MOVE_CAMERA_LEFT,  new MouseAxisTrigger(MouseInput.AXIS_X, true ));
        }
        
        @Override
        public void apply() {
            applyWith(MOVE_CAMERA_RIGHT, MOVE_CAMERA_LEFT, MOVE_CAMERA_UP, MOVE_CAMERA_DOWN);
        }

        @Override
        public void onAnalog(String name, float value, float tpf) {
            final float SPEED = 4f;
            switch (name) {
                case MOVE_CAMERA_RIGHT:
                    horizontalRotation(-SPEED, tpf);
                    break;
                case MOVE_CAMERA_LEFT:
                    horizontalRotation(SPEED, tpf);
                    break;
                case MOVE_CAMERA_UP:
                    verticalRotation(-SPEED / 2, tpf);
                    break;
                case MOVE_CAMERA_DOWN:
                    verticalRotation(SPEED / 2, tpf);
                    break;
            }
        }

        private void horizontalRotation(float amount, float tpf) {
            yaw += amount * tpf;
            updateCamera();
        }

        private void verticalRotation(float amount, float tpf) {
            pitch -= amount * tpf;
            pitch = FastMath.clamp(+pitch, -FastMath.HALF_PI + 0.01f, +FastMath.HALF_PI - 0.01f);
            updateCamera();
        }

        private void updateCamera() {
            float x = FastMath.cos(pitch) * FastMath.sin(yaw);
            float z = FastMath.cos(pitch) * FastMath.cos(yaw);
            Vector3f dir = new Vector3f(x, FastMath.sin(pitch), z);
            mCam.lookAtDirection(dir.normalizeLocal(), Vector3f.UNIT_Y);
        }
    }

    private final class AttackListener extends AbstractListener implements ActionListener {
        private static final String SHOOT = "PlayerShoot";
        
        public AttackListener(InputManager im) {
            super(im);
            im.addMapping(SHOOT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
            im.addMapping(SHOOT, new KeyTrigger(KeyInput.KEY_RCONTROL));
        }

        @Override
        public void apply() {
            applyWith(SHOOT);
        }

        @Override 
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                mSpheres.add(constructSphere());
            }
        }

        private Sphere constructSphere() {
            return new Sphere(context, mCam.getDirection(), Player.this, mEnemy);
        }
    }
}
