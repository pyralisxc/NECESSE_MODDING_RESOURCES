/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.GameWindow;

public interface GameLoopListener {
    public void frameTick(TickManager var1, GameWindow var2);

    public void drawTick(TickManager var1);

    public boolean isDisposed();
}

