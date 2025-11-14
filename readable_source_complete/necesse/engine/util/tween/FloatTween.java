/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import necesse.engine.util.tween.ValueTween;

public class FloatTween
extends ValueTween<Float, FloatTween> {
    public FloatTween(double duration, float initialValue, float endValue) {
        super(duration, Float.valueOf(initialValue), Float.valueOf(endValue));
    }

    public FloatTween(float initialValue) {
        super(Float.valueOf(initialValue));
    }

    public FloatTween(FloatTween existingTween, double duration, float endValue) {
        super(existingTween, duration, Float.valueOf(endValue));
    }

    @Override
    public FloatTween newTween(double duration, Float endValue) {
        return new FloatTween(this, duration, endValue.floatValue());
    }

    @Override
    protected void tween(double percent) {
        this.setValue(Float.valueOf((float)((double)((Float)this.startValue).floatValue() + percent * (double)(((Float)this.endValue).floatValue() - ((Float)this.startValue).floatValue()))));
    }
}

