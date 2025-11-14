/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import necesse.engine.GameLog;
import necesse.engine.util.tween.Playable;

public abstract class Tween<T extends Tween<T>>
extends Playable<T> {
    protected double duration = 0.0;

    public Tween(double duration) {
        this.setDuration(duration);
    }

    @Override
    protected void progress(double percent) {
        this.tween(percent);
    }

    @Override
    protected void progressBackwards(double percent) {
        this.tween(1.0 - percent);
    }

    protected abstract void tween(double var1);

    @Override
    public double getCycleDuration() {
        return this.duration;
    }

    public T setDuration(double duration) {
        if (this.isRunning()) {
            GameLog.warn.println("Cannot change duration while tween is running");
            return (T)((Tween)this.self());
        }
        if (duration < 0.0) {
            GameLog.warn.println("Tween duration must be greater than or equal to 0");
            duration = 0.0;
        }
        this.duration = duration;
        return (T)((Tween)this.self());
    }
}

