/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Iterator;
import java.util.function.Function;

public class MapIterator<T, R>
implements Iterator<R> {
    public final Iterator<T> iterator;
    public final Function<T, R> mapper;

    public MapIterator(Iterator<T> iterator, Function<T, R> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public R next() {
        return this.mapper.apply(this.iterator.next());
    }
}

