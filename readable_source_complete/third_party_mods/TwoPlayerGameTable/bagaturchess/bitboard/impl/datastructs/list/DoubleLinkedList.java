/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs.list;

import bagaturchess.bitboard.impl.datastructs.list.ListNodeObject;

public class DoubleLinkedList<V> {
    ListNodeObject<V> first;
    ListNodeObject<V> last;
    int maxSize;
    int curSize;
    long garbage;

    public DoubleLinkedList(int _maxSize) {
        this.maxSize = _maxSize;
        this.curSize = 0;
    }

    public void moveToHead(ListNodeObject<V> node) {
        if (node != this.first) {
            if (node == this.last) {
                this.last = node.prev;
            }
            node.bypassNeighbours();
            this.addHead(node);
        }
    }

    public ListNodeObject<V> addHead(long key, ListNodeObject<V> node) {
        ++this.curSize;
        if (this.curSize > this.maxSize) {
            throw new IllegalStateException("curSize " + this.curSize + ", maxSize " + this.maxSize);
        }
        return this.addHead(node);
    }

    public ListNodeObject<V> addHead(long key, Object value) {
        ++this.curSize;
        if (this.curSize > this.maxSize) {
            throw new IllegalStateException("curSize " + this.curSize + ", maxSize " + this.maxSize);
        }
        return this.addHead(new ListNodeObject<Object>(key, value));
    }

    private ListNodeObject<V> addHead(ListNodeObject<V> node) {
        ListNodeObject<V> firstNode = this.first;
        this.first = node;
        if (firstNode != null) {
            this.first.next = firstNode;
            firstNode.prev = this.first;
        } else {
            if (this.last != null) {
                throw new IllegalStateException("last != null");
            }
            this.last = this.first;
        }
        return this.first;
    }

    public ListNodeObject<V> removeLast() {
        ListNodeObject<V> removed = null;
        if (this.last != null) {
            --this.curSize;
            ++this.garbage;
            removed = this.last;
            this.last = removed.prev;
            if (this.last != null) {
                this.last.next = null;
            } else {
                this.first = null;
            }
            removed.prev = null;
        } else {
            if (this.curSize != 0) {
                throw new IllegalStateException("curSize != 0");
            }
            if (this.first != null) {
                throw new IllegalStateException("first != null");
            }
        }
        return removed;
    }

    public ListNodeObject<V> removeHead() {
        ListNodeObject<V> removed = null;
        if (this.first != null) {
            --this.curSize;
            ++this.garbage;
            removed = this.first;
            this.first = removed.next;
            if (this.first != null) {
                this.first.prev = null;
            } else {
                this.last = null;
            }
            removed.next = null;
        } else {
            if (this.curSize != 0) {
                throw new IllegalStateException("curSize != 0 " + this.curSize);
            }
            if (this.first != null) {
                throw new IllegalStateException("first != null");
            }
        }
        return removed;
    }

    public ListNodeObject<V> lastToHead() {
        ListNodeObject<V> newFirst = this.last;
        if (this.last != null && this.last != this.first) {
            ListNodeObject prevLast = this.last.prev;
            prevLast.next = null;
            this.last.prev = null;
            if (prevLast == this.first) {
                this.first.next = null;
                this.first.prev = newFirst;
                newFirst.next = this.first;
                this.last = this.first;
                this.first = newFirst;
            } else {
                this.last = prevLast;
                newFirst.next = this.first;
                this.first.prev = newFirst;
                this.first = newFirst;
            }
        }
        return newFirst;
    }

    public int size() {
        return this.curSize;
    }
}

