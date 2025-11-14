/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.HashMap;
import necesse.engine.util.MapQueue;

public class HashMapQueue<K, V>
extends MapQueue<K, V> {
    public HashMapQueue() {
        super(new HashMap());
    }
}

