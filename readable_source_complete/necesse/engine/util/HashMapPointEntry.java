/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

public interface HashMapPointEntry<K, V> {
    public int getX();

    public int getY();

    public K getKey();

    public V getValue();

    public V setValue(V var1);
}

