/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Iterator;

public class ForIterator
implements Iterator<Integer> {
    public final int startIndex;
    public final int endIndex;
    public final int delta;
    private int current;

    public ForIterator(int startIndex, int endIndex, int delta) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.current = startIndex;
        this.delta = ForIterator.getDelta(startIndex, endIndex, delta);
    }

    public ForIterator(int startIndex, int endIndex) {
        this(startIndex, endIndex, 1);
    }

    private static int getDelta(int startIndex, int endIndex, int delta) {
        if (delta == 0) {
            throw new IllegalArgumentException("Delta cannot be 0");
        }
        if (endIndex < startIndex) {
            return -Math.abs(delta);
        }
        return Math.abs(delta);
    }

    @Override
    public boolean hasNext() {
        if (this.delta < 0) {
            return this.current >= this.endIndex;
        }
        return this.current <= this.endIndex;
    }

    @Override
    public Integer next() {
        int next = this.current;
        this.current += this.delta;
        return next;
    }

    public Iterable<Integer> iterable() {
        return () -> this;
    }
}

