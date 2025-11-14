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

public class PointHashMap<V> {
    protected HashMap<Long, V> map;

    public PointHashMap() {
        this.map = new HashMap();
    }

    public PointHashMap(int initialCapacity) {
        this.map = new HashMap(initialCapacity);
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(int x, int y) {
        return this.map.containsKey(GameMath.getUniqueLongKey(x, y));
    }

    public boolean containsValue(V value) {
        return this.map.containsValue(value);
    }

    public V put(int x, int y, V value) {
        return this.map.put(GameMath.getUniqueLongKey(x, y), value);
    }

    public V get(int x, int y) {
        return this.map.get(GameMath.getUniqueLongKey(x, y));
    }

    public V getOrDefault(int x, int y, V defaultValue) {
        return this.map.getOrDefault(GameMath.getUniqueLongKey(x, y), defaultValue);
    }

    public V remove(int x, int y) {
        return this.map.remove(GameMath.getUniqueLongKey(x, y));
    }

    public void clear() {
        this.map.clear();
    }

    public V compute(int x, int y, RemappingFunction<V> remappingFunction) {
        return (V)this.map.compute(GameMath.getUniqueLongKey(x, y), (key, value) -> remappingFunction.remap(x, y, value));
    }

    public V compute(int x, int y, PointRemappingFunction<V> remappingFunction) {
        return (V)this.map.compute(GameMath.getUniqueLongKey(x, y), (key, value) -> remappingFunction.remap(new Point(x, y), value));
    }

    public Iterable<Point> getKeys() {
        return GameUtils.mapIterable(this.map.keySet().iterator(), key -> new Point(GameMath.getXFromUniqueLongKey(key), GameMath.getYFromUniqueLongKey(key)));
    }

    public Stream<Point> streamKeys() {
        return this.map.keySet().stream().map(key -> new Point(GameMath.getXFromUniqueLongKey(key), GameMath.getYFromUniqueLongKey(key)));
    }

    public Iterable<HashMapPointEntry<Point, V>> getEntries() {
        return GameUtils.mapIterable(this.map.entrySet().iterator(), this::mapEntry);
    }

    public Stream<HashMapPointEntry<Point, V>> streamEntries() {
        return this.map.entrySet().stream().map(this::mapEntry);
    }

    public void forEach(ForEachFunction<V> action) {
        this.map.forEach((? super K key, ? super V value) -> {
            int x = GameMath.getXFromUniqueLongKey(key);
            int y = GameMath.getYFromUniqueLongKey(key);
            action.handle(x, y, value);
        });
    }

    private HashMapPointEntry<Point, V> mapEntry(final Map.Entry<Long, V> entry) {
        return new HashMapPointEntry<Point, V>(){

            @Override
            public int getX() {
                return GameMath.getXFromUniqueLongKey((Long)entry.getKey());
            }

            @Override
            public int getY() {
                return GameMath.getYFromUniqueLongKey((Long)entry.getKey());
            }

            @Override
            public Point getKey() {
                Long key = (Long)entry.getKey();
                int x = GameMath.getXFromUniqueLongKey(key);
                int y = GameMath.getYFromUniqueLongKey(key);
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

    @FunctionalInterface
    public static interface ForEachFunction<V> {
        public void handle(int var1, int var2, V var3);
    }
}

