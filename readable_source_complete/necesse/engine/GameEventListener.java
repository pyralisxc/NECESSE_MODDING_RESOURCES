/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.GameEventInterface;

public abstract class GameEventListener<T>
implements GameEventInterface<T> {
    private boolean isDisposed;
    private Runnable disposeLogic;

    @Override
    public void init(Runnable disposeLogic) {
        this.disposeLogic = disposeLogic;
    }

    @Override
    public boolean isDisposed() {
        return this.isDisposed;
    }

    public void dispose() {
        this.isDisposed = true;
        if (this.disposeLogic != null) {
            this.disposeLogic.run();
        }
    }
}

