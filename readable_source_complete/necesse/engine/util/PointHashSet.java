/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.HashSet;
import java.util.stream.Collector;
import necesse.engine.util.PointSetAbstract;

public class PointHashSet
extends PointSetAbstract<HashSet<Long>> {
    public PointHashSet() {
        super(new HashSet());
    }

    public PointHashSet(int initialCapacity) {
        super(new HashSet(initialCapacity));
    }

    public PointHashSet(PointSetAbstract<?> set) {
        this(set.size());
        this.addAll(set);
    }

    public static Collector<Point, ?, PointHashSet> collector() {
        return PointSetAbstract.collector(PointHashSet::new);
    }
}

