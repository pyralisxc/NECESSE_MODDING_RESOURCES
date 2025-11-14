/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.ArrayList;
import necesse.engine.util.HashMapCollection;

public class HashMapArrayList<K, V>
extends HashMapCollection<K, V, ArrayList<V>> {
    public HashMapArrayList() {
        super(ArrayList::new);
    }

    public HashMapArrayList(int initialSize) {
        super(() -> new ArrayList(initialSize));
    }
}

