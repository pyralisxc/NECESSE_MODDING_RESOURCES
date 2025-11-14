/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameTool;

import necesse.engine.GlobalData;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.gameTooltips.GameTooltips;

public interface GameTool {
    default public void init() {
    }

    default public void tick() {
    }

    public boolean inputEvent(InputEvent var1);

    default public boolean controllerEvent(ControllerEvent event) {
        return false;
    }

    default public void onPaused() {
    }

    default public void onRenewed() {
    }

    default public boolean canCancel() {
        return true;
    }

    default public boolean forceControllerCursor() {
        return this.canCancel();
    }

    default public boolean startControllerCursor() {
        return this.forceControllerCursor();
    }

    default public boolean shouldForceControllerMenuLayer() {
        return true;
    }

    default public boolean shouldShowWires() {
        return false;
    }

    public void isCancelled();

    public void isCleared();

    public GameTooltips getTooltips();

    default public GameWindow.CURSOR getCursor() {
        return null;
    }

    default public int getMouseX() {
        return WindowManager.getWindow().mousePos().sceneX + GlobalData.getCurrentState().getCamera().getX();
    }

    default public int getMouseTileX() {
        return GameMath.getTileCoordinate(this.getMouseX());
    }

    default public int getMouseY() {
        return WindowManager.getWindow().mousePos().sceneY + GlobalData.getCurrentState().getCamera().getY();
    }

    default public int getMouseTileY() {
        return GameMath.getTileCoordinate(this.getMouseY());
    }
}

