/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;

public class PointSetAbstract<T extends Set<Long>>
implements Iterable<Point> {
    protected T set;

    public PointSetAbstract(T set) {
        this.set = set;
    }

    public static long toKey(int x, int y) {
        return GameMath.getUniqueLongKey(x, y);
    }

    public static int getX(long key) {
        return GameMath.getXFromUniqueLongKey(key);
    }

    public static int getY(long key) {
        return GameMath.getYFromUniqueLongKey(key);
    }

    public static Point toPoint(long key) {
        return new Point(PointSetAbstract.getX(key), PointSetAbstract.getY(key));
    }

    public int size() {
        return this.set.size();
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public boolean contains(int x, int y) {
        return this.set.contains(PointSetAbstract.toKey(x, y));
    }

    public boolean add(int x, int y) {
        return this.set.add(PointSetAbstract.toKey(x, y));
    }

    public boolean addAll(PointSetAbstract<?> other) {
        boolean changed = false;
        Iterator iterator = other.set.iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            changed |= this.set.add(key);
        }
        return changed;
    }

    public boolean remove(int x, int y) {
        return this.set.remove(PointSetAbstract.toKey(x, y));
    }

    public void clear() {
        this.set.clear();
    }

    @Override
    public Iterator<Point> iterator() {
        return GameUtils.mapIterator(this.set.iterator(), PointSetAbstract::toPoint);
    }

    public void forEach(BiConsumer<Integer, Integer> consumer) {
        Iterator iterator = this.set.iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            consumer.accept(PointSetAbstract.getX(key), PointSetAbstract.getY(key));
        }
    }

    public Stream<Point> stream() {
        return this.set.stream().map(PointSetAbstract::toPoint);
    }

    public Iterable<Long> getKeys() {
        return this.set;
    }

    public T getUnderlyingSet() {
        return this.set;
    }

    public static <T extends PointSetAbstract<?>> Collector<Point, ?, T> collector(Supplier<T> supplier) {
        return Collector.of(supplier, (set, point) -> set.add(point.x, point.y), (set1, set2) -> {
            set1.addAll((PointSetAbstract<?>)set2);
            return set1;
        }, new Collector.Characteristics[0]);
    }
}

