/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

public class ComparableSequence<T extends Comparable<? super T>>
implements Comparable<ComparableSequence<T>> {
    private ComparableSequence<T> before;
    private ComparableSequence<T> then;
    private final T compareObject;

    public ComparableSequence(T compareObject) {
        this.compareObject = compareObject;
    }

    public ComparableSequence<T> beforeBy(ComparableSequence<T> compareSequence) {
        compareSequence.then = this;
        if (this.before != null) {
            compareSequence.before = this.before;
            this.before.then = compareSequence;
        }
        this.before = compareSequence;
        return compareSequence;
    }

    public ComparableSequence<T> beforeBy(T compareObject) {
        return this.beforeBy((T)new ComparableSequence<T>(compareObject));
    }

    public ComparableSequence<T> thenBy(ComparableSequence<T> compareSequence) {
        compareSequence.before = this;
        if (this.then != null) {
            compareSequence.then = this.then;
            this.then.before = compareSequence;
        }
        this.then = compareSequence;
        return compareSequence;
    }

    public ComparableSequence<T> thenBy(T compareObject) {
        return this.thenBy((T)new ComparableSequence<T>(compareObject));
    }

    public ComparableSequence<T> firstBy(ComparableSequence<T> compareSequence) {
        return this.getFirst().beforeBy(compareSequence);
    }

    public ComparableSequence<T> firstBy(T compareObject) {
        return this.getFirst().beforeBy(compareObject);
    }

    public ComparableSequence<T> lastBy(ComparableSequence<T> compareSequence) {
        return this.getLast().thenBy(compareSequence);
    }

    public ComparableSequence<T> lastBy(T compareObject) {
        return this.getLast().thenBy(compareObject);
    }

    private ComparableSequence<T> getFirst() {
        if (this.before != null) {
            return super.getFirst();
        }
        return this;
    }

    private ComparableSequence<T> getLast() {
        if (this.then != null) {
            return super.getLast();
        }
        return this;
    }

    @Override
    public int compareTo(ComparableSequence<T> o) {
        ComparableSequence<T> me = this.getFirst();
        ComparableSequence<T> him = super.getFirst();
        int c = me.compareObject.compareTo(him.compareObject);
        while (c == 0) {
            if (me.then == null && him.then == null) {
                return c;
            }
            me = me.then;
            him = him.then;
            if (me == null) {
                return 1;
            }
            if (him == null) {
                return -1;
            }
            c = me.compareObject.compareTo(him.compareObject);
        }
        return c;
    }
}

