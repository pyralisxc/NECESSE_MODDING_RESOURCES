/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop;

import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.gameLoop.GameLoopListener;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.GameWindow;

public abstract class GameLoop
extends TickManager {
    private boolean stopMainGameLoop;
    private LinkedList<GameLoopListener> gameLoopListeners = new LinkedList();

    public GameLoop(String name, int maxFPS) {
        super(name, maxFPS);
    }

    public void runMainGameLoop() {
        while (!this.stopMainGameLoop) {
            this.tickLogic();
        }
    }

    public void stopMainGameLoop() {
        this.stopMainGameLoop = true;
    }

    public void addGameLoopListener(GameLoopListener listener) {
        this.gameLoopListeners.add(listener);
    }

    public void runGameLoopListenersFrameTick(GameWindow window) {
        ListIterator iterator = this.gameLoopListeners.listIterator();
        while (iterator.hasNext()) {
            GameLoopListener next = (GameLoopListener)iterator.next();
            if (next.isDisposed()) {
                iterator.remove();
                continue;
            }
            next.frameTick(this, window);
        }
    }

    public void runGameLoopListenersDrawTick() {
        ListIterator iterator = this.gameLoopListeners.listIterator();
        while (iterator.hasNext()) {
            GameLoopListener next = (GameLoopListener)iterator.next();
            if (next.isDisposed()) {
                iterator.remove();
                continue;
            }
            next.drawTick(this);
        }
    }
}

