/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.function.Supplier;

public class ComputedValue<V> {
    private Supplier<V> supplier;
    private V value;

    public ComputedValue(Supplier<V> supplier) {
        this.supplier = supplier;
    }

    public V get() {
        if (this.supplier == null) {
            return this.value;
        }
        this.value = this.supplier.get();
        this.supplier = null;
        return this.value;
    }
}

