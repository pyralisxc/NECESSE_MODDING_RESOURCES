/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Map;
import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;

public class MapQueue<K, V> {
    private final Map<K, GameLinkedList.Element> map;
    private final GameLinkedList<QueuedElement> queue = new GameLinkedList();

    public MapQueue(Map<K, GameLinkedList.Element> map) {
        this.map = map;
    }

    public synchronized void addLast(K key, V value) {
        this.map.compute(key, (k, e) -> {
            if (e != null && !e.isRemoved()) {
                e.remove();
            }
            return this.queue.addLast(new QueuedElement(key, value));
        });
    }

    public synchronized void addFirst(K key, V value) {
        this.map.compute(key, (k, e) -> {
            if (e != null && !e.isRemoved()) {
                e.remove();
            }
            return this.queue.addFirst(new QueuedElement(key, value));
        });
    }

    public synchronized V getOrDefault(K key, Supplier<V> defaultValue) {
        GameLinkedList.Element value = this.map.get(key);
        return value == null ? defaultValue.get() : ((QueuedElement)value.object).value;
    }

    public synchronized V getOrDefault(K key, V defaultValue) {
        return (V)this.getOrDefault(key, (V)((Supplier<Object>)() -> defaultValue));
    }

    public synchronized V get(K key) {
        return this.getOrDefault(key, (V)null);
    }

    public synchronized boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    public synchronized V getFirst() {
        if (this.queue.isEmpty()) {
            return null;
        }
        return this.queue.getFirst().value;
    }

    public synchronized V removeFirst() {
        QueuedElement element = this.queue.removeFirst();
        if (element != null) {
            this.map.remove(element.key);
            return element.value;
        }
        return null;
    }

    public synchronized V getLast() {
        if (this.queue.isEmpty()) {
            return null;
        }
        return this.queue.getLast().value;
    }

    public synchronized V removeLast() {
        QueuedElement element = this.queue.removeLast();
        if (element != null) {
            this.map.remove(element.key);
            return element.value;
        }
        return null;
    }

    public synchronized void remove(K key) {
        GameLinkedList.Element value = this.map.remove(key);
        if (value != null && !value.isRemoved()) {
            value.remove();
        }
    }

    public synchronized boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public synchronized int size() {
        return this.queue.size();
    }

    private class QueuedElement {
        public final K key;
        public final V value;

        public QueuedElement(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}

