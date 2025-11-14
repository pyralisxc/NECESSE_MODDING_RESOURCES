/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public abstract class HashMapCollectionAbstract<K, V, L> {
    private final HashMap<K, L> map = new HashMap();

    public void add(K key, V value) {
        this.map.compute(key, (k, l) -> {
            if (l == null) {
                l = this.createNewList();
            }
            this.addToList(l, value);
            return l;
        });
    }

    public void addAll(K key, Collection<V> values) {
        this.map.compute(key, (k, l) -> {
            if (l == null) {
                l = this.createNewList();
            }
            this.addAllToList(l, values);
            return l;
        });
    }

    public L clear(K key) {
        return this.map.remove(key);
    }

    public void clearAll() {
        this.map.clear();
    }

    public Stream<V> stream(K key) {
        L list = this.map.get(key);
        if (list == null) {
            return Stream.empty();
        }
        return this.streamValues(list);
    }

    public L get(K key) {
        return (L)this.map.compute(key, (k, l) -> {
            if (l == null) {
                l = this.createNewList();
            }
            return l;
        });
    }

    public boolean contains(K key, V value) {
        L list = this.map.get(key);
        return list != null && this.listContains(list, value);
    }

    public boolean remove(K key, V value) {
        L list = this.map.get(key);
        if (list != null && this.removeFromList(list, value)) {
            if (this.isListEmpty(list)) {
                this.map.remove(key);
            }
            return true;
        }
        return false;
    }

    public boolean removeAll(K key, Collection<V> values) {
        L list = this.map.get(key);
        if (list != null && this.removeAllFromList(list, values)) {
            if (this.isListEmpty(list)) {
                this.map.remove(key);
            }
            return true;
        }
        return false;
    }

    public int removeValues(V value) {
        HashSet<K> mapRemoves = new HashSet<K>();
        int out = 0;
        for (Object key : this.map.keySet()) {
            L list = this.map.get(key);
            if (!this.removeFromList(list, value)) continue;
            ++out;
            if (!this.isListEmpty(list)) continue;
            mapRemoves.add(key);
        }
        for (Object key : mapRemoves) {
            this.map.remove(key);
        }
        return out;
    }

    public boolean isEmpty(K key) {
        L list = this.map.get(key);
        return list == null || this.isListEmpty(list);
    }

    public int getSize(K key) {
        L list = this.map.get(key);
        return list == null ? 0 : this.getListSize(list);
    }

    public int getSize() {
        return this.map.size();
    }

    public Set<K> keySet() {
        return this.map.keySet();
    }

    public Collection<L> values() {
        return this.map.values();
    }

    public Set<Map.Entry<K, L>> entrySet() {
        return this.map.entrySet();
    }

    protected abstract L createNewList();

    protected abstract void addToList(L var1, V var2);

    protected abstract void addAllToList(L var1, Collection<V> var2);

    protected abstract Stream<V> streamValues(L var1);

    protected abstract boolean listContains(L var1, V var2);

    protected abstract boolean isListEmpty(L var1);

    protected abstract int getListSize(L var1);

    protected abstract boolean removeFromList(L var1, V var2);

    protected abstract boolean removeAllFromList(L var1, Collection<V> var2);
}

