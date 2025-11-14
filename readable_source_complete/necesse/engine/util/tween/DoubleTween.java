/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import necesse.engine.util.tween.ValueTween;

public class DoubleTween
extends ValueTween<Double, DoubleTween> {
    public DoubleTween(double duration, double initialValue, double endValue) {
        super(duration, Double.valueOf(initialValue), Double.valueOf(endValue));
    }

    public DoubleTween(double initialValue) {
        super(Double.valueOf(initialValue));
    }

    public DoubleTween(DoubleTween existingTween, double duration, double endValue) {
        super(existingTween, duration, Double.valueOf(endValue));
    }

    @Override
    protected void tween(double percent) {
        this.setValue((Double)this.startValue + percent * ((Double)this.endValue - (Double)this.startValue));
    }

    @Override
    public DoubleTween newTween(double duration, Double endValue) {
        return new DoubleTween(this, duration, (double)endValue);
    }
}

