/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import necesse.engine.util.GameMath;

public class PointSearchList {
    protected LinkedList<Long> open = new LinkedList();
    protected HashSet<Long> closed = new HashSet();

    public PointSearchList() {
    }

    public PointSearchList(int startX, int startY) {
        this.addFirst(startX, startY);
    }

    public void addLast(int x, int y) {
        this.open.addLast(GameMath.getUniqueLongKey(x, y));
    }

    public void addFirst(int x, int y) {
        this.open.addFirst(GameMath.getUniqueLongKey(x, y));
    }

    public boolean isEmpty() {
        return this.open.isEmpty();
    }

    public Point removeFirst() {
        long key = this.open.removeFirst();
        return new Point(GameMath.getXFromUniqueLongKey(key), GameMath.getYFromUniqueLongKey(key));
    }

    public Point removeFirstAndAddClosed() {
        long key = this.open.removeFirst();
        this.closed.add(key);
        return new Point(GameMath.getXFromUniqueLongKey(key), GameMath.getYFromUniqueLongKey(key));
    }

    public void addClosed(int x, int y) {
        this.closed.add(GameMath.getUniqueLongKey(x, y));
    }

    public boolean isClosed(int x, int y) {
        return this.closed.contains(GameMath.getUniqueLongKey(x, y));
    }

    public int getOpenSize() {
        return this.open.size();
    }

    public int getClosedSize() {
        return this.closed.size();
    }
}

