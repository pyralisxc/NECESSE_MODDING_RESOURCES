/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapPointEntry;

public class ShortPointHashMap<V> {
    protected HashMap<Integer, V> map = new HashMap();

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(int x, int y) {
        return this.map.containsKey(GameMath.getUniqueIntKey(x, y));
    }

    public boolean containsValue(V value) {
        return this.map.containsValue(value);
    }

    public V put(int x, int y, V value) {
        return this.map.put(GameMath.getUniqueIntKey(x, y), value);
    }

    public V get(int x, int y) {
        return this.map.get(GameMath.getUniqueIntKey(x, y));
    }

    public V getOrDefault(int x, int y, V defaultValue) {
        return this.map.getOrDefault(GameMath.getUniqueIntKey(x, y), defaultValue);
    }

    public V remove(int x, int y) {
        return this.map.remove(GameMath.getUniqueIntKey(x, y));
    }

    public boolean containsKey(Point point) {
        return this.containsKey(point.x, point.y);
    }

    public V put(Point point, V value) {
        return this.put(point.x, point.y, value);
    }

    public V get(Point point) {
        return this.get(point.x, point.y);
    }

    public V getOrDefault(Point point, V defaultValue) {
        return this.getOrDefault(point.x, point.y, defaultValue);
    }

    public V remove(Point point) {
        return this.remove(point.x, point.y);
    }

    public void clear() {
        this.map.clear();
    }

    public V compute(int x, int y, RemappingFunction<V> remappingFunction) {
        return (V)this.map.compute(GameMath.getUniqueIntKey(x, y), (key, value) -> remappingFunction.remap(x, y, value));
    }

    public V compute(int x, int y, PointRemappingFunction<V> remappingFunction) {
        return (V)this.map.compute(GameMath.getUniqueIntKey(x, y), (key, value) -> remappingFunction.remap(new Point(x, y), value));
    }

    public Iterable<HashMapPointEntry<Point, V>> getEntries() {
        return GameUtils.mapIterable(this.map.entrySet().iterator(), this::mapEntry);
    }

    public Stream<HashMapPointEntry<Point, V>> streamEntries() {
        return this.map.entrySet().stream().map(this::mapEntry);
    }

    private HashMapPointEntry<Point, V> mapEntry(final Map.Entry<Integer, V> entry) {
        return new HashMapPointEntry<Point, V>(){

            @Override
            public int getX() {
                return GameMath.getXFromUniqueIntKey((Integer)entry.getKey());
            }

            @Override
            public int getY() {
                return GameMath.getYFromUniqueIntKey((Integer)entry.getKey());
            }

            @Override
            public Point getKey() {
                Integer key = (Integer)entry.getKey();
                int x = GameMath.getXFromUniqueIntKey(key);
                int y = GameMath.getYFromUniqueIntKey(key);
                return new Point(x, y);
            }

            @Override
            public V getValue() {
                return entry.getValue();
            }

            @Override
            public V setValue(V value) {
                return entry.setValue(value);
            }
        };
    }

    public Collection<V> values() {
        return this.map.values();
    }

    @FunctionalInterface
    public static interface RemappingFunction<V> {
        public V remap(int var1, int var2, V var3);
    }

    @FunctionalInterface
    public static interface PointRemappingFunction<V> {
        public V remap(Point var1, V var2);
    }
}

