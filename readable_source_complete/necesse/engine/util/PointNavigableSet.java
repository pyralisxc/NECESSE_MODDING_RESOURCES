/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.Iterator;
import java.util.NavigableSet;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointSortedSet;

public class PointNavigableSet
extends PointSortedSet
implements NavigableSet<Point> {
    protected NavigableSet<Long> navigableSet;

    public PointNavigableSet(NavigableSet<Long> set) {
        super(set);
        this.navigableSet = set;
    }

    @Override
    public Point lower(Point point) {
        Long key = this.navigableSet.lower(this.toKey(point.x, point.y));
        if (key == null) {
            return null;
        }
        return this.toPoint(key);
    }

    @Override
    public Point floor(Point point) {
        Long key = this.navigableSet.floor(this.toKey(point.x, point.y));
        if (key == null) {
            return null;
        }
        return this.toPoint(key);
    }

    @Override
    public Point ceiling(Point point) {
        Long key = this.navigableSet.ceiling(this.toKey(point.x, point.y));
        if (key == null) {
            return null;
        }
        return this.toPoint(key);
    }

    @Override
    public Point higher(Point point) {
        Long key = this.navigableSet.higher(this.toKey(point.x, point.y));
        if (key == null) {
            return null;
        }
        return this.toPoint(key);
    }

    @Override
    public Point pollFirst() {
        Long key = this.navigableSet.pollFirst();
        if (key == null) {
            return null;
        }
        return this.toPoint(key);
    }

    @Override
    public Point pollLast() {
        Long key = this.navigableSet.pollLast();
        if (key == null) {
            return null;
        }
        return this.toPoint(key);
    }

    @Override
    public NavigableSet<Point> descendingSet() {
        return new PointNavigableSet(this.navigableSet.descendingSet());
    }

    @Override
    public Iterator<Point> descendingIterator() {
        return GameUtils.mapIterator(this.navigableSet.descendingIterator(), this::toPoint);
    }

    @Override
    public NavigableSet<Point> subSet(Point fromElement, boolean fromInclusive, Point toElement, boolean toInclusive) {
        NavigableSet<Long> subSet = this.navigableSet.subSet(this.toKey(fromElement.x, fromElement.y), fromInclusive, this.toKey(toElement.x, toElement.y), toInclusive);
        return new PointNavigableSet(subSet);
    }

    @Override
    public NavigableSet<Point> headSet(Point toElement, boolean inclusive) {
        NavigableSet<Long> subSet = this.navigableSet.headSet(this.toKey(toElement.x, toElement.y), inclusive);
        return new PointNavigableSet(subSet);
    }

    @Override
    public NavigableSet<Point> tailSet(Point fromElement, boolean inclusive) {
        NavigableSet<Long> subSet = this.navigableSet.tailSet(this.toKey(fromElement.x, fromElement.y), inclusive);
        return new PointNavigableSet(subSet);
    }
}

