/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import java.util.function.Consumer;

public abstract class GameAreaSink<T_OUT>
implements Consumer<T_OUT> {
    private boolean cancelled;

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}

