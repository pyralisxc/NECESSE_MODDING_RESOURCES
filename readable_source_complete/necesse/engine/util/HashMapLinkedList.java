/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.LinkedList;
import necesse.engine.util.HashMapCollection;

public class HashMapLinkedList<K, V>
extends HashMapCollection<K, V, LinkedList<V>> {
    public HashMapLinkedList() {
        super(LinkedList::new);
    }
}

