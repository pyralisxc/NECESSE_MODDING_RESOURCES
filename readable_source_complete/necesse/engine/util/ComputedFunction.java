/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.function.Function;

public class ComputedFunction<T, R> {
    private Function<T, R> function;
    private R result;

    public ComputedFunction(Function<T, R> function) {
        this.function = function;
    }

    public boolean isComputed() {
        return this.function == null;
    }

    public R get(T value) {
        if (this.function == null) {
            return this.result;
        }
        this.result = this.function.apply(value);
        this.function = null;
        return this.result;
    }
}

