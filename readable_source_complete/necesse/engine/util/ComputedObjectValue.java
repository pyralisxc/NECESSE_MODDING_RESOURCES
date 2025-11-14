/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.function.Supplier;
import necesse.engine.util.ComputedValue;

public class ComputedObjectValue<T, V>
extends ComputedValue<V> {
    public final T object;

    public ComputedObjectValue(T object, Supplier<V> supplier) {
        super(supplier);
        this.object = object;
    }
}

