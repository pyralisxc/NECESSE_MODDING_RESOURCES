/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.concurrent.ConcurrentHashMap;
import necesse.engine.util.MapQueue;

public class ConcurrentHashMapQueue<K, V>
extends MapQueue<K, V> {
    public ConcurrentHashMapQueue() {
        super(new ConcurrentHashMap());
    }
}

