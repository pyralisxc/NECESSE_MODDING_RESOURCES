/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashMapCollectionAbstract;

public class HashMapGameLinkedList<K, V>
extends HashMapCollectionAbstract<K, V, GameLinkedList<V>> {
    @Override
    protected GameLinkedList<V> createNewList() {
        return new GameLinkedList();
    }

    @Override
    protected void addToList(GameLinkedList<V> collection, V value) {
        collection.addLast(value);
    }

    @Override
    protected void addAllToList(GameLinkedList<V> collection, Collection<V> values) {
        collection.addAll(values);
    }

    @Override
    protected Stream<V> streamValues(GameLinkedList<V> collection) {
        return collection.stream();
    }

    @Override
    protected boolean listContains(GameLinkedList<V> collection, V value) {
        return collection.contains(value);
    }

    @Override
    protected boolean isListEmpty(GameLinkedList<V> collection) {
        return collection.isEmpty();
    }

    @Override
    protected int getListSize(GameLinkedList<V> collection) {
        return collection.size();
    }

    @Override
    protected boolean removeFromList(GameLinkedList<V> collection, V value) {
        return collection.remove(value);
    }

    @Override
    protected boolean removeAllFromList(GameLinkedList<V> collection, Collection<V> values) {
        return collection.removeAll(values);
    }

    public Stream<GameLinkedList.Element> streamElements(K key) {
        return ((GameLinkedList)this.get(key)).streamElements();
    }

    public Iterator<GameLinkedList.Element> elementIterator(K key) {
        return ((GameLinkedList)this.get(key)).elementIterator();
    }

    public Iterable<GameLinkedList.Element> elements(K key) {
        return ((GameLinkedList)this.get(key)).elements();
    }

    public GameLinkedList.Element addFirst(K key, V value) {
        return ((GameLinkedList)this.get(key)).addFirst(value);
    }

    public GameLinkedList.Element addLast(K key, V value) {
        return ((GameLinkedList)this.get(key)).addLast(value);
    }

    public V getFirst(K key) {
        return (V)((GameLinkedList)this.get(key)).getFirst();
    }

    public V getLast(K key) {
        return (V)((GameLinkedList)this.get(key)).getLast();
    }

    public GameLinkedList.Element getFirstElement(K key) {
        return ((GameLinkedList)this.get(key)).getFirstElement();
    }

    public GameLinkedList.Element getLastElement(K key) {
        return ((GameLinkedList)this.get(key)).getLastElement();
    }
}

