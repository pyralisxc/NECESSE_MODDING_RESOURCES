/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.HashSet;
import necesse.engine.util.HashMapCollection;

public class HashMapSet<K, V>
extends HashMapCollection<K, V, HashSet<V>> {
    public HashMapSet() {
        super(HashSet::new);
    }
}

