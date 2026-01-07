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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This is the Main Class of your Game. It should boot up your game and do initial initialisation
 * Move your Logic into AppStates or Controls or other java classes
 */
public class Game extends SimpleApplication {
    public static final float GRAVITY = 9.81f;

    public static BitmapText sScoreText;

    private final List<Spatial> mMap = new ArrayList<>();

    private Enemy mEnemy;
    private Spatial mThing;
    private Player mPlayer;

    public Geometry createLine(Vector3f from, Vector3f to) {
        Line line = new Line(from, to);
        Geometry geom = new Geometry("line"+from+to, line);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);

        return geom;
    }

    @Override
    public void simpleInitApp() {
        initCrosshair();
        flyCam.setEnabled(false);

        sScoreText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"));
        sScoreText.setColor(ColorRGBA.White);
        sScoreText.setSize(sScoreText.getFont().getCharSet().getRenderedSize() * 2f);
        sScoreText.setText("Score: 0");
        sScoreText.setLocalTranslation(10, settings.getHeight() - 10, 0);
        getContext().getMouseInput().setCursorVisible(false);

        initMap();
        mPlayer = new Player(inputManager, cam, mMap, assetManager, rootNode);
        mEnemy = new Enemy(assetManager, mMap, mPlayer, rootNode);
        mPlayer.setEnemy(mEnemy);

        mThing = assetManager.loadModel("Models/Object.obj");
        rootNode.attachChild(mThing);
        guiNode.attachChild(sScoreText);
    }

    private void initCrosshair() {
        Function<Vector3f, Vector3f> cs = vec -> {
            Vector3f out = new Vector3f(settings.getWindowWidth()/2f, settings.getWindowHeight()/2f, 0);
            if(vec.x == 1f) {
                out.x += vec.z;
            }
            if(vec.y == 1f) {
                out.y += vec.z;
            }
            return out;
        };
        Geometry line1 = createLine(
                cs.apply(new Vector3f(1, 0, 10)),
                cs.apply(new Vector3f(1, 0, -10))
        );
        Geometry line2 = createLine(
                cs.apply(new Vector3f(0, 1, 10)),
                cs.apply(new Vector3f(0, 1, -10))
        );
        guiNode.attachChild(line1);
        guiNode.attachChild(line2);
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

        Spatial floor = assetManager.loadModel("Models/Floor.obj");
        floor.move(0, -4, 0);
        rootNode.attachChild(floor);

        Spatial wall1 = assetManager.loadModel("Models/Wall.obj");
        wall1.move(10, 0, 0);
        rootNode.attachChild(wall1);

        Spatial wall2 = assetManager.loadModel("Models/Wall.obj");
        wall2.move(-10, 0, 0);
        rootNode.attachChild(wall2);

        Spatial stair1 = assetManager.loadModel("Models/Stairs.obj");
        stair1.move(0, -3, -15);
        rootNode.attachChild(stair1);

        mMap.add(floor);
        mMap.add(wall1);
        mMap.add(wall2);
        mMap.add(stair1);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //this method will be called every game tick and can be used to make updates
        mThing.rotate(0, tpf, 0);
        mPlayer.update(tpf);
        mEnemy.update(tpf);

        if(mEnemy.getBound().intersects(mPlayer.getBound())) {
            stop();
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //add render code here (if any)
    }
}
