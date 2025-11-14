/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    public R apply(A var1, B var2, C var3);

    default public <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> after.apply((R)this.apply(a, b, c));
    }
}

