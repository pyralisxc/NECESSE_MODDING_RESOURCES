/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import necesse.engine.util.tween.ValueTween;

public class IntTween
extends ValueTween<Integer, IntTween> {
    public IntTween(double duration, int initialValue, int endValue) {
        super(duration, Integer.valueOf(initialValue), Integer.valueOf(endValue));
    }

    public IntTween(int initialValue) {
        super(Integer.valueOf(initialValue));
    }

    public IntTween(IntTween existingTween, double duration, int endValue) {
        super(existingTween, duration, Integer.valueOf(endValue));
    }

    @Override
    protected void tween(double percent) {
        this.setValue((int)Math.round((double)((Integer)this.startValue).intValue() + percent * (double)((Integer)this.endValue - (Integer)this.startValue)));
    }

    @Override
    public IntTween newTween(double duration, Integer endValue) {
        return new IntTween(this, duration, (int)endValue);
    }
}

