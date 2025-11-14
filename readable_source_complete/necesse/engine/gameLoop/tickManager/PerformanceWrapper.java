/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop.tickManager;

public abstract class PerformanceWrapper {
    private boolean hasEnded;

    protected abstract void endLogic();

    public final void end() {
        if (this.hasEnded) {
            throw new IllegalStateException("Wrapper has already ended");
        }
        this.hasEnded = true;
        this.endLogic();
    }
}

