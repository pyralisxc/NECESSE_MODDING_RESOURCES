/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import java.util.ArrayList;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.util.tween.Tween;

public abstract class ValueTween<V, T extends ValueTween<V, T>>
extends Tween<T> {
    protected ArrayList<Consumer<V>> onValueChangedConsumers;
    protected T existingTween;
    protected V startValue;
    protected V endValue;
    protected V value;

    public ValueTween(double duration, V initialValue, V endValue) {
        super(duration);
        this.value = initialValue;
        this.startValue = initialValue;
        this.endValue = endValue;
    }

    public ValueTween(V initialValue) {
        this(0.0, initialValue, initialValue);
    }

    public ValueTween(T existingTween, double duration, V endValue) {
        this(duration, ((ValueTween)existingTween).getValue(), endValue);
        this.existingTween = existingTween;
        this.value = ((ValueTween)existingTween).getValue();
    }

    public abstract T newTween(double var1, V var3);

    public V updateAndGet(double currentTime) {
        this.update(currentTime);
        return this.value;
    }

    public V getValue() {
        if (this.existingTween != null) {
            return ((ValueTween)this.existingTween).value;
        }
        return this.value;
    }

    public T setValue(V value) {
        if (this.value != value && this.onValueChangedConsumers != null) {
            this.onValueChangedConsumers.forEach((Consumer<Consumer<V>>)((Consumer<Consumer>)consumer -> consumer.accept(value)));
        }
        this.value = value;
        if (this.existingTween != null) {
            ((ValueTween)this.existingTween).setValue(value);
        }
        return (T)((ValueTween)this.self());
    }

    public T setInitialValue(V value) {
        if (this.existingTween != null) {
            GameLog.err.println("Cannot set initial value of tween that is based on another tween");
            return (T)((ValueTween)this.self());
        }
        this.startValue = value;
        return (T)((ValueTween)this.self());
    }

    public T onValueChanged(Consumer<V> consumer) {
        if (this.onValueChangedConsumers == null) {
            this.onValueChangedConsumers = new ArrayList(1);
            this.onValueChangedConsumers.add(consumer);
        }
        return (T)((ValueTween)this.self());
    }

    @Override
    protected void progressToCompletion() {
        this.value = this.endValue;
    }

    @Override
    protected void progressToBeginning() {
        this.value = this.startValue;
    }

    @Override
    protected void prepareBackwardsPlay() {
    }

    @Override
    protected void preparePlay() {
        this.startValue = this.existingTween != null ? ((ValueTween)this.existingTween).value : this.value;
    }
}

