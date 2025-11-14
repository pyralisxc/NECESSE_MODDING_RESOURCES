/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.camera;

import java.awt.geom.Point2D;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.MainGameCamera;

public class MainGameFollowCamera
extends MainGameCamera {
    @Override
    public void tickCamera(TickManager tickManager, MainGame mainGame, Client client) {
        PlayerMob player = client.getPlayer();
        if (player != null) {
            float zoomAmount;
            this.centerCamera(player.getDrawX(), player.getDrawY());
            if (mainGame.isRunning() && player.getSelectedItem() != null && (zoomAmount = player.getSelectedItem().item.zoomAmount()) != 0.0f) {
                float yDir;
                float xDir;
                if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                    xDir = ControllerInput.getAimX();
                    yDir = ControllerInput.getAimY();
                } else {
                    GameWindow window = WindowManager.getWindow();
                    xDir = (float)window.mousePos().sceneX / (float)window.getSceneWidth() * 2.0f - 1.0f;
                    yDir = (float)window.mousePos().sceneY / (float)window.getSceneHeight() * 2.0f - 1.0f;
                }
                this.setPosition(this.getX() + (int)(xDir * zoomAmount), this.getY() + (int)(yDir * zoomAmount));
            }
        }
        Point2D.Float cameraShake = client.getCurrentCameraShake();
        this.setPosition(this.getX() + (int)cameraShake.x, this.getY() + (int)cameraShake.y);
    }
}

