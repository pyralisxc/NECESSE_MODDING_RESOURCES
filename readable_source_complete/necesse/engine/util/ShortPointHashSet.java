/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;

public class ShortPointHashSet
implements Iterable<Point> {
    protected HashSet<Integer> set = new HashSet();

    public int size() {
        return this.set.size();
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public boolean contains(int x, int y) {
        return this.set.contains(GameMath.getUniqueIntKey(x, y));
    }

    public void add(int x, int y) {
        this.set.add(GameMath.getUniqueIntKey(x, y));
    }

    public void remove(int x, int y) {
        this.set.remove(GameMath.getUniqueIntKey(x, y));
    }

    public boolean contains(Point point) {
        return this.contains(point.x, point.y);
    }

    public void add(Point point) {
        this.add(point.x, point.y);
    }

    public void remove(Point point) {
        this.remove(point.x, point.y);
    }

    public void clear() {
        this.set.clear();
    }

    @Override
    public Iterator<Point> iterator() {
        return GameUtils.mapIterator(this.set.iterator(), key -> {
            int x = GameMath.getXFromUniqueLongKey(key.intValue());
            int y = GameMath.getYFromUniqueLongKey(key.intValue());
            return new Point(x, y);
        });
    }

    public void forEach(BiConsumer<Integer, Integer> consumer) {
        Iterator<Integer> iterator = this.set.iterator();
        while (iterator.hasNext()) {
            long key = iterator.next().intValue();
            int x = GameMath.getXFromUniqueLongKey(key);
            int y = GameMath.getYFromUniqueLongKey(key);
            consumer.accept(x, y);
        }
    }

    public Stream<Point> stream() {
        return this.set.stream().map(key -> {
            int x = GameMath.getXFromUniqueLongKey(key.intValue());
            int y = GameMath.getYFromUniqueLongKey(key.intValue());
            return new Point(x, y);
        });
    }

    public Iterable<Integer> getKeys() {
        return this.set;
    }
}

