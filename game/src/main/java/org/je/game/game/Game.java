package org.je.game.game;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Supplier;

public class Game extends SimpleApplication implements Context {
    public static final float GRAVITY = 9.81f;
    public static BitmapText sScoreText;

    private final List<Spatial> mMap = new ArrayList<>();
    private Enemy mEnemy;
    private Spatial mThing;
    private Player mPlayer;

    @Override
    public void simpleInitApp() {
        new CrosshairInitializer(this, 10).attachToGui();
        flyCam.setEnabled(false);

        sScoreText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"));
        sScoreText.setColor(ColorRGBA.White);
        sScoreText.setSize(sScoreText.getFont().getCharSet().getRenderedSize() * 2f);
        sScoreText.setText("Score: 0");
        sScoreText.setLocalTranslation(10, settings.getHeight() - 10, 0);
        guiNode.attachChild(sScoreText);
        
        getContext().getMouseInput().setCursorVisible(false);

        initMap();
        mEnemy = new Enemy(this);
        mPlayer = new Player(this, cam, mEnemy);
    }

    private void initMap() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-7, -7, -7).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection(new Vector3f(10, -10, 10).normalizeLocal());
        sun2.setColor(ColorRGBA.White);
        rootNode.addLight(sun2);

        DirectionalLight sun3 = new DirectionalLight();
        sun3.setDirection(new Vector3f(10, 10, 10).normalizeLocal());
        sun3.setColor(ColorRGBA.White);
        rootNode.addLight(sun3);

        Spatial floor0 = assetManager.loadModel("Models/Floor.obj");
        floor0.move(-69, -2, -40);
        rootNode.attachChild(floor0);
        mMap.add(floor0);

        Spatial floor = assetManager.loadModel("Models/Floor.obj");
        floor.move(0, -4, 0);
        rootNode.attachChild(floor);
        mMap.add(floor);

        Spatial floor1 = assetManager.loadModel("Models/Floor.obj");
        floor1.move(69, -6, 40);
        rootNode.attachChild(floor1);
        mMap.add(floor1);

        Spatial floor2 = assetManager.loadModel("Models/Floor.obj");
        floor2.move(69*2, -8, 40*2);
        rootNode.attachChild(floor2);
        mMap.add(floor2);

        mThing = assetManager.loadModel("Models/Object.obj");
        rootNode.attachChild(mThing);
    }

    @Override
    public void simpleUpdate(float tpf) {
        mThing.rotate(0, tpf, 0);
        mPlayer.update(tpf);
        mEnemy.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

    @Override
    public InputManager inputManager() {
        return inputManager;
    }
    
    @Override
    public AssetManager assetManager() {
        return assetManager;
    }

    @Override
    public Node rootNode() {
        return rootNode;
    }

    @Override
    public Node guiNode() {
        return guiNode;
    }

    @Override
    public Player player() {
        return mPlayer;
    }

    @Override
    public boolean hasCollisionWithMap(AbstractEntity entity) {
        for (Spatial spt : mMap) {
            if (spt.getWorldBound().intersects(entity.getBound())) 
                return true;
        }
        return false;
    }
    
    @Override
    public boolean hasCollisionWithEntity(AbstractEntity entity) {
        if (mEnemy.getBound().intersects(entity.getBound())) {
            return true;
        }
        return false;
    }

    @Override
    public void gameOver() {
        stop();
    }

    public int getWidth() {
        return settings.getWindowWidth();
    }

    public int getHeight() {
        return settings.getWindowHeight();
    }

    private final class CrosshairInitializer {
        private final Context context;
        private final float mSize;

        public CrosshairInitializer(Context context, float size) {
            this.context = context;
            mSize = size;
        }

        public Vector3f centerVector() {
            return new Vector3f(context.getWidth()/2, context.getHeight()/2, 0);
        }

        public Vector3f[] createVectorSet(Vector3f axis) {
            Vector3f vecA = centerVector();
            Vector3f vecB = centerVector();

            if (Vector3f.UNIT_X.equals(axis)) {
                vecA.x -= mSize;
                vecB.x += mSize;
            } else if (Vector3f.UNIT_Y.equals(axis)) {
                vecA.y -= mSize;
                vecB.y += mSize;
            } else {
                throw new IllegalArgumentException("Unknown axis.");
            }

            return new Vector3f[] { vecA, vecB };
        }
        
        public Geometry createLine(Vector3f from, Vector3f to) {
            Line line = new Line(from, to);
            Geometry geom = new Geometry(String.format("Line[from: %s to: %s]", from.toString(), to.toString()), line);

            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.White);
            geom.setMaterial(mat);

            return geom;
        }

        public void attachToGui() {
            Vector3f[] vecsX = createVectorSet(Vector3f.UNIT_X);
            Vector3f[] vecsY = createVectorSet(Vector3f.UNIT_Y);
            guiNode.attachChild(createLine(vecsX[0], vecsX[1]));
            guiNode.attachChild(createLine(vecsY[0], vecsY[1]));
        }
    }
}
