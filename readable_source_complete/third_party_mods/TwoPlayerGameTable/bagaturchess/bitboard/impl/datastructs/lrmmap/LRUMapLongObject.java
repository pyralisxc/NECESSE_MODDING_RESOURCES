/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs.lrmmap;

import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.HashMapLongObject;
import bagaturchess.bitboard.impl.datastructs.IValuesVisitor_HashMapLongObject;
import bagaturchess.bitboard.impl.datastructs.list.DoubleLinkedList;
import bagaturchess.bitboard.impl.datastructs.list.ListNodeObject;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;

public class LRUMapLongObject<T> {
    private int FACTOR = 1;
    private int MIN_MAXSIZE = 111;
    private IBinarySemaphore semaphore;
    private DataObjectFactory<T> factory;
    protected int maxSize;
    protected int curSize;
    protected DoubleLinkedList<T> list;
    protected HashMapLongObject<ListNodeObject<T>> map;
    private boolean full = false;
    private boolean disabeCreation;
    private long gets = 0L;
    private long gets_ok = 0L;
    private double hitrate = 0.0;

    protected LRUMapLongObject(DataObjectFactory<T> _factory, int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
        this(_factory, _maxSize, fillWithDummyEntries, _semaphore, false);
    }

    protected LRUMapLongObject(DataObjectFactory<T> _factory, int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore, boolean _disabeCreation) {
        this.initBySize(_factory, _maxSize);
        if (fillWithDummyEntries) {
            this.fillWithDummyEntries(_maxSize);
        }
        this.semaphore = _semaphore;
        this.disabeCreation = _disabeCreation;
    }

    public void visitValues(final IValuesVisitor_HashMapLongObject<T> visitor) {
        IValuesVisitor_HashMapLongObject nodesVisitor = new IValuesVisitor_HashMapLongObject<ListNodeObject<T>>(){

            @Override
            public void visit(ListNodeObject<T> value) {
                visitor.visit(value.getValue());
            }
        };
        this.map.visitValues(nodesVisitor);
    }

    private void initBySize(DataObjectFactory<T> _factory, int _maxSize) {
        if (_maxSize < this.MIN_MAXSIZE) {
            _maxSize = this.MIN_MAXSIZE;
        }
        this.factory = _factory;
        this.maxSize = _maxSize;
        this.curSize = 0;
        this.list = new DoubleLinkedList(this.maxSize);
        this.map = new HashMapLongObject(this.FACTOR * this.maxSize);
    }

    private void fillWithDummyEntries(int count) {
        for (int i = 0; i < count; ++i) {
            this.associateEntry((long)(9.223372036854776E18 * Math.random()));
        }
    }

    public void lock() {
        this.semaphore.lock();
    }

    public void unlock() {
        this.semaphore.unlock();
    }

    protected void addHeadEntry(ListNodeObject<T> node) {
        ++this.curSize;
        if (this.curSize >= this.maxSize) {
            this.full = true;
        }
        this.map.put(node.getKey(), node);
        this.list.addHead(node.getKey(), node);
    }

    protected ListNodeObject<T> removeHeadEntry() {
        if (this.list.size() != this.curSize) {
            throw new IllegalStateException("list.size()=" + this.list.size() + ", curSize=" + this.curSize);
        }
        ListNodeObject<T> node = this.list.removeHead();
        if (node != null) {
            this.map.remove(node.getKey());
            --this.curSize;
            if (this.curSize < this.maxSize) {
                this.full = false;
            }
            node.clearNeighbours();
            return node;
        }
        return null;
    }

    protected T get(long key) {
        ListNodeObject<T> node = this.map.get(key);
        if (node != null) {
            return node.getValue();
        }
        return null;
    }

    protected T getAndUpdateLRU(long key) {
        ListNodeObject<T> node = this.map.get(key);
        ++this.gets;
        if (node != null) {
            ++this.gets_ok;
            this.hitrate = (double)this.gets_ok / (double)this.gets;
            this.list.moveToHead(node);
            return node.getValue();
        }
        return null;
    }

    public int getHitRate() {
        return (int)(100.0 * this.hitrate);
    }

    public int getUsage() {
        return (int)(100.0 * ((double)this.curSize / (double)this.maxSize));
    }

    protected T associateEntry(long key) {
        if (!this.full) {
            if (!this.disabeCreation) {
                ++this.curSize;
                if (this.curSize >= this.maxSize) {
                    this.full = true;
                }
                T data = this.factory.createObject();
                ListNodeObject<T> newNode = this.list.addHead(key, data);
                this.map.put(key, newNode);
                return data;
            }
            return null;
        }
        ListNodeObject<T> entry = this.list.lastToHead();
        long hashkey = entry.getKey();
        this.map.remove(hashkey);
        entry.setKey(key);
        this.map.put(key, entry);
        return entry.getValue();
    }

    public void clear() {
        this.curSize = 0;
        this.list = new DoubleLinkedList(this.maxSize);
        this.map = new HashMapLongObject(this.FACTOR * this.maxSize);
        this.full = false;
    }

    public int getCurrentSize() {
        return this.curSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }
}

