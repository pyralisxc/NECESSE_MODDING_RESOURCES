/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.state;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.window.GameWindow;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormComponent;

public abstract class State {
    public boolean isInitialized = false;
    private boolean isRunning = true;
    private boolean isDisposed = false;

    public abstract void frameTick(TickManager var1, GameWindow var2);

    public abstract void secondTick(TickManager var1);

    public abstract void drawScene(TickManager var1, boolean var2);

    public abstract void drawSceneOverlay(TickManager var1);

    public abstract void drawHud(TickManager var1);

    public abstract void onWindowResized(GameWindow var1);

    public abstract FormManager getFormManager();

    public Stream<Rectangle> streamHudHitboxes() {
        FormManager formManager = this.getFormManager();
        if (formManager != null) {
            return formManager.getComponentList().stream().filter(FormComponent::shouldDraw).flatMap(c -> c.getHitboxes().stream());
        }
        return Stream.empty();
    }

    public void onClientDrawnLevelChanged() {
    }

    public abstract void reloadInterfaceFromSettings(boolean var1);

    public abstract GameCamera getCamera();

    public abstract SoundEmitter getALListener();

    public abstract void onClose();

    public abstract void onCrash(List<Throwable> var1);

    public void dispose() {
        this.isDisposed = true;
    }

    public boolean isDisposed() {
        return this.isDisposed;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}

