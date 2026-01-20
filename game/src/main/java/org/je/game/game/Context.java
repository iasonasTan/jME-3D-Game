package org.je.game.game;

import com.jme3.scene.Node;

import org.je.game.game.Player;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;

public interface Context {
    InputManager inputManager();
    
    AssetManager assetManager();

    Node rootNode();
    
    Node guiNode();

    boolean hasCollisionWithMap(AbstractEntity entity);

    boolean hasCollisionWithEntity(AbstractEntity entity);

    Player player();

    void gameOver();

    int getWidth();

    int getHeight();
}
