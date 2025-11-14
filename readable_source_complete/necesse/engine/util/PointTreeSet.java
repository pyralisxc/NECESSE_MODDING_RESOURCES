/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collector;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointNavigableSet;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.PointSortedSet;
import necesse.engine.util.Zoning;

public class PointTreeSet
extends PointSetAbstract<TreeSet<Long>>
implements Zoning.ToRectangleInterface {
    public PointTreeSet(Comparator<Point> comparator) {
        super(new TreeSet((key1, key2) -> {
            int x1 = GameMath.getXFromUniqueLongKey(key1);
            int y1 = GameMath.getYFromUniqueLongKey(key1);
            int x2 = GameMath.getXFromUniqueLongKey(key2);
            int y2 = GameMath.getYFromUniqueLongKey(key2);
            return comparator.compare(new Point(x1, y1), new Point(x2, y2));
        }));
    }

    public static Comparator<Point> firstYThenXComparator() {
        Comparator<Point> comparator = Comparator.comparingInt(p -> p.y);
        comparator = comparator.thenComparingInt(p -> p.x);
        return comparator;
    }

    public NavigableSet<Point> subSet(Point fromElement, boolean fromInclusive, Point toElement, boolean toInclusive) {
        return new PointNavigableSet(((TreeSet)this.set).subSet(PointTreeSet.toKey(fromElement.x, fromElement.y), fromInclusive, PointTreeSet.toKey(toElement.x, toElement.y), toInclusive));
    }

    public NavigableSet<Point> headSet(Point toElement, boolean inclusive) {
        return new PointNavigableSet(((TreeSet)this.set).headSet(PointTreeSet.toKey(toElement.x, toElement.y), inclusive));
    }

    public NavigableSet<Point> tailSet(Point fromElement, boolean inclusive) {
        return new PointNavigableSet(((TreeSet)this.set).tailSet(PointTreeSet.toKey(fromElement.x, fromElement.y), inclusive));
    }

    public SortedSet<Point> subSet(Point fromElement, Point toElement) {
        return new PointSortedSet(((TreeSet)this.set).subSet(PointTreeSet.toKey(fromElement.x, fromElement.y), PointTreeSet.toKey(toElement.x, toElement.y)));
    }

    public SortedSet<Point> headSet(Point toElement) {
        return new PointSortedSet(((TreeSet)this.set).headSet(PointTreeSet.toKey(toElement.x, toElement.y)));
    }

    public SortedSet<Point> tailSet(Point fromElement) {
        return new PointSortedSet(((TreeSet)this.set).tailSet(PointTreeSet.toKey(fromElement.x, fromElement.y)));
    }

    public NavigableSet<Point> ascendingSet() {
        return new PointNavigableSet((NavigableSet)this.set);
    }

    public NavigableSet<Point> descendingSet() {
        return new PointNavigableSet(((TreeSet)this.set).descendingSet());
    }

    @Override
    public Point first() {
        return PointTreeSet.toPoint((Long)((TreeSet)this.set).first());
    }

    public Point last() {
        return PointTreeSet.toPoint((Long)((TreeSet)this.set).last());
    }

    public Point lower(int x, int y) {
        Long key = ((TreeSet)this.set).lower(GameMath.getUniqueLongKey(x, y));
        if (key == null) {
            return null;
        }
        return PointTreeSet.toPoint(key);
    }

    public Point floor(int x, int y) {
        Long key = ((TreeSet)this.set).floor(GameMath.getUniqueLongKey(x, y));
        if (key == null) {
            return null;
        }
        return PointTreeSet.toPoint(key);
    }

    public Point ceiling(int x, int y) {
        Long key = ((TreeSet)this.set).ceiling(GameMath.getUniqueLongKey(x, y));
        if (key == null) {
            return null;
        }
        return PointTreeSet.toPoint(key);
    }

    @Override
    public Point higher(int x, int y) {
        Long key = ((TreeSet)this.set).higher(GameMath.getUniqueLongKey(x, y));
        if (key == null) {
            return null;
        }
        return PointTreeSet.toPoint(key);
    }

    public Point pollFirst() {
        Long first = (Long)((TreeSet)this.set).pollFirst();
        if (first == null) {
            return null;
        }
        return PointTreeSet.toPoint(first);
    }

    public Point pollLast() {
        Long first = (Long)((TreeSet)this.set).pollLast();
        if (first == null) {
            return null;
        }
        return PointTreeSet.toPoint(first);
    }

    public static Collector<Point, ?, PointTreeSet> collector(Comparator<Point> comparator) {
        return PointSetAbstract.collector(() -> new PointTreeSet(comparator));
    }
}

