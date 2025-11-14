/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AreaSpliterator<T>
implements Spliterator<T> {
    protected int startX;
    protected int lastX;
    protected int lastY;
    protected int cX;
    protected int cY;
    protected boolean valid;

    public AreaSpliterator(int startX, int startY, int lastX, int lastY) {
        this.reset(startX, startY, lastX, lastY);
    }

    protected AreaSpliterator() {
    }

    protected void reset(int startX, int startY, int endX, int endY) {
        this.valid = startX < endX && startY < endY;
        this.startX = startX;
        this.lastX = endX;
        this.lastY = endY;
        this.cX = startX;
        this.cY = startY;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (this.cX >= this.lastX) {
            this.cX = this.startX;
            ++this.cY;
        }
        if (this.cY < this.lastY) {
            action.accept(this.getPos(this.cX, this.cY));
            ++this.cX;
            return true;
        }
        return false;
    }

    protected abstract T getPos(int var1, int var2);

    @Override
    public Spliterator<T> trySplit() {
        if (!this.valid) {
            return null;
        }
        int yRemaining = this.lastY - this.cY - 1;
        if (yRemaining <= 0) {
            int xRemaining = this.lastX - this.cX - 1;
            if (xRemaining > 0) {
                int halfX = this.cX + xRemaining / 2 + 1;
                final AreaSpliterator me = this;
                AreaSpliterator split = new AreaSpliterator<T>(halfX, this.cY, this.lastX, this.lastY){

                    @Override
                    protected T getPos(int x, int y) {
                        return me.getPos(x, y);
                    }
                };
                this.lastX = halfX;
                return split;
            }
            return null;
        }
        int halfY = this.cY + yRemaining / 2 + 1;
        final AreaSpliterator me = this;
        AreaSpliterator split = new AreaSpliterator<T>(this.startX, halfY, this.lastX, this.lastY){

            @Override
            protected T getPos(int x, int y) {
                return me.getPos(x, y);
            }
        };
        this.lastY = halfY;
        return split;
    }

    @Override
    public long estimateSize() {
        if (!this.valid) {
            return 0L;
        }
        int xRemaining = this.lastX - this.cX;
        int width = this.lastX - this.startX;
        int yRemaining = this.lastY - this.cY - 1;
        return (long)xRemaining + (long)yRemaining * (long)width;
    }

    @Override
    public int characteristics() {
        return 17489;
    }

    public static <T> Stream<T> stream(Spliterator<T> spliterator, boolean parallel) {
        return StreamSupport.stream(spliterator, parallel);
    }

    public static <T> Stream<T> stream(Spliterator<T> spliterator) {
        return AreaSpliterator.stream(spliterator, false);
    }

    public Stream<T> stream(boolean parallel) {
        return AreaSpliterator.stream(this, parallel);
    }

    public Stream<T> stream() {
        return AreaSpliterator.stream(this);
    }
}

