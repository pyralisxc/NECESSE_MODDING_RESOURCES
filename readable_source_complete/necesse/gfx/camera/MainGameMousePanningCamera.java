/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.camera;

import java.awt.geom.Point2D;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.camera.MainGamePanningCamera;

public class MainGameMousePanningCamera
extends MainGamePanningCamera {
    private final float speedModifier;

    public MainGameMousePanningCamera(int x, int y, float speedModifier) {
        super(x, y);
        this.speedModifier = speedModifier;
    }

    @Override
    public void tickCamera(TickManager tickManager, MainGame mainGame, Client client) {
        if (Settings.hideUI || !mainGame.formManager.isMouseOver()) {
            GameWindow window = WindowManager.getWindow();
            float fMouseX = (float)window.mousePos().sceneX / (float)window.getSceneWidth() * 2.0f - 1.0f;
            float fMouseY = (float)window.mousePos().sceneY / (float)window.getSceneHeight() * 2.0f - 1.0f;
            this.setSpeed((float)(new Point2D.Float(fMouseX, fMouseY).distance(0.0, 0.0) * (double)this.speedModifier));
            this.setDirection(fMouseX, fMouseY);
        }
        super.tickCamera(tickManager, mainGame, client);
    }
}

