/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

@FunctionalInterface
public interface EaseFunction {
    public double ease(double var1);

    default public float ease(float p) {
        return (float)this.ease((double)p);
    }
}

