/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import necesse.engine.util.HashProxyLinkedList;

public class HashLinkedList<T>
extends HashProxyLinkedList<T, T> {
    public HashLinkedList() {
        super(t -> t);
    }
}

