/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.util.HashMapCollectionAbstract;

public class HashMapCollection<K, V, L extends Collection<V>>
extends HashMapCollectionAbstract<K, V, L> {
    private Supplier<L> listCreator;

    public HashMapCollection(Supplier<L> listCreator) {
        this.listCreator = listCreator;
    }

    @Override
    protected L createNewList() {
        return (L)((Collection)this.listCreator.get());
    }

    @Override
    protected void addToList(L collection, V value) {
        collection.add(value);
    }

    @Override
    protected void addAllToList(L collection, Collection<V> values) {
        collection.addAll(values);
    }

    @Override
    protected Stream<V> streamValues(L collection) {
        return collection.stream();
    }

    @Override
    protected boolean listContains(L collection, V value) {
        return collection.contains(value);
    }

    @Override
    protected boolean isListEmpty(L collection) {
        return collection.isEmpty();
    }

    @Override
    protected int getListSize(L collection) {
        return collection.size();
    }

    @Override
    protected boolean removeFromList(L collection, V value) {
        return collection.remove(value);
    }

    @Override
    protected boolean removeAllFromList(L collection, Collection<V> values) {
        return collection.removeAll(values);
    }
}

