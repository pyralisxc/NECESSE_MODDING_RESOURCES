/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class ValueWatcher<T> {
    private final Supplier<T> getter;
    private T last;

    public ValueWatcher(Supplier<T> getter) {
        this.getter = getter;
        this.last = getter.get();
    }

    public void update() {
        T next = this.getter.get();
        if (this.hasChanged(this.last, next)) {
            this.last = next;
            this.onChange(next);
        }
    }

    protected boolean hasChanged(T last, T next) {
        return !Objects.equals(last, next);
    }

    public abstract void onChange(T var1);
}

