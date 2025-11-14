/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs.list;

public class ListNodeObject<V> {
    ListNodeObject<V> prev;
    ListNodeObject<V> next;
    long key;
    V value;

    public ListNodeObject(long _key, V _value) {
        this.key = _key;
        this.value = _value;
    }

    public void bypassNeighbours() {
        ListNodeObject<V> prevNode = this.prev;
        ListNodeObject<V> nextNode = this.next;
        this.prev = null;
        this.next = null;
        if (prevNode != null) {
            prevNode.next = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        }
    }

    public V getValue() {
        return this.value;
    }

    public void setValue(V newValue) {
        this.value = newValue;
    }

    public long getKey() {
        return this.key;
    }

    public void setKey(long _key) {
        this.key = _key;
    }

    public void clearNeighbours() {
        this.next = null;
        this.prev = null;
    }
}

