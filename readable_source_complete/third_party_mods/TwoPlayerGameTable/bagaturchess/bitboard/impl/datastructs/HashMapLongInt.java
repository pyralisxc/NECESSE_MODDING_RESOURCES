/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs;

import bagaturchess.bitboard.impl.datastructs.PrimeGenerator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class HashMapLongInt
implements Serializable {
    static final long serialVersionUID = 1660087326306111301L;
    public static final float LOAD_FACTOR = 0.75f;
    public static final int INITIAL_CAPACITY = 13;
    public static final int GROW_STEP = 2;
    protected static final int LAST = -1;
    public static final int NO_VALUE = -123456789;
    protected int growStep;
    protected int growSimpl;
    protected float loadFactor;
    protected int simplIndex;
    protected int limit;
    protected int capacity;
    protected transient int nextFree;
    protected transient long[] keys;
    protected transient int[] elements;
    protected transient int[] nextPtr;
    protected int count;

    public HashMapLongInt() {
        this(13, 2, 0.75f);
    }

    public HashMapLongInt(int initialCapacity) {
        this(initialCapacity, 2, 0.75f);
    }

    public HashMapLongInt(int initialCapacity, int growStep, float loadFactor) {
        if ((double)loadFactor > 1.0 || loadFactor <= 0.0f) {
            throw new IllegalArgumentException("Load Factor = " + loadFactor);
        }
        if (growStep <= 1) {
            throw new IllegalArgumentException("Grow step = " + growStep);
        }
        this.growStep = growStep;
        this.growSimpl = growStep == 2 ? 4 : (growStep < 10 ? growStep + 4 : 13);
        this.loadFactor = loadFactor;
        this.simplIndex = 0;
        this.init(initialCapacity);
    }

    public int size() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public int getArraySize() {
        return this.elements.length;
    }

    public long[] getAllKeys() {
        int index = 0;
        long[] result = new long[this.count];
        for (int i = 0; i < this.capacity; ++i) {
            int pos = this.nextPtr[i];
            while (pos != -1) {
                result[index++] = this.keys[pos - this.capacity];
                pos = this.nextPtr[pos];
            }
        }
        return result;
    }

    public int[] getAllValues() {
        int index = 0;
        int[] result = new int[this.count];
        for (int i = 0; i < this.capacity; ++i) {
            int pos = this.nextPtr[i];
            while (pos != -1) {
                result[index++] = this.elements[pos - this.capacity];
                pos = this.nextPtr[pos];
            }
        }
        return result;
    }

    public boolean contains(int value) {
        for (int i = 0; i < this.capacity; ++i) {
            int pos = this.nextPtr[i];
            while (pos != -1) {
                if (this.elements[pos - this.capacity] == value) {
                    return true;
                }
                pos = this.nextPtr[pos];
            }
        }
        return false;
    }

    public boolean containsValue(int value) {
        return this.contains(value);
    }

    public boolean containsKey(long key) {
        int pos = this.nextPtr[HashMapLongInt.hash(key) % this.capacity];
        while (pos != -1) {
            if (this.keys[pos - this.capacity] == key) {
                return true;
            }
            pos = this.nextPtr[pos];
        }
        return false;
    }

    public void clear() {
        int i;
        for (i = 0; i < this.capacity; ++i) {
            this.nextPtr[i] = -1;
        }
        i = this.capacity;
        while (i < this.nextPtr.length) {
            this.nextPtr[i++] = i;
        }
        this.nextFree = this.capacity;
        this.count = 0;
    }

    public Object clone() {
        HashMapLongInt result = null;
        try {
            result = (HashMapLongInt)super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        result.keys = new long[this.keys.length];
        result.elements = new int[this.elements.length];
        result.nextPtr = new int[this.nextPtr.length];
        System.arraycopy(this.nextPtr, 0, result.nextPtr, 0, this.nextPtr.length);
        System.arraycopy(this.keys, 0, result.keys, 0, this.keys.length);
        System.arraycopy(this.elements, 0, result.elements, 0, this.elements.length);
        return result;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof HashMapLongInt)) {
            return false;
        }
        HashMapLongInt t = (HashMapLongInt)object;
        if (t.count != this.count) {
            return false;
        }
        for (int i = 0; i < this.capacity; ++i) {
            int pos = this.nextPtr[i];
            while (pos != -1) {
                int index = pos - this.capacity;
                int temp = t.get(this.keys[index]);
                if (temp == -123456789) {
                    return false;
                }
                if (this.elements[index] != temp) {
                    return false;
                }
                pos = this.nextPtr[pos];
            }
        }
        return true;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.count;
        result = 37 * result + this.capacity;
        result = 37 * result + this.arrayHashCode(this.nextPtr);
        result = 37 * result + this.arrayHashCode(this.keys);
        result = 37 * result + this.arrayHashCode(this.elements);
        return result;
    }

    private int arrayHashCode(int[] array) {
        if (array == null) {
            return 0;
        }
        int result = 17;
        for (int i = 0; i < array.length; ++i) {
            result = 37 * result + array[i];
        }
        return result;
    }

    private int arrayHashCode(long[] array) {
        if (array == null) {
            return 0;
        }
        int result = 17;
        for (int i = 0; i < array.length; ++i) {
            result = 37 * result + (int)(array[i] ^ array[i] >>> 32);
        }
        return result;
    }

    public void shrink() {
        this.shrink(0.75f);
    }

    public void shrink(float shrinkFactor) {
        if (shrinkFactor <= 0.0f || (double)shrinkFactor > 1.0) {
            throw new IllegalArgumentException("Shrink Factor = " + shrinkFactor);
        }
        int newCapacity = (int)((float)this.count / (this.loadFactor * shrinkFactor));
        long l = PrimeGenerator.getClosestPrime(newCapacity);
        this.simplIndex = (int)(l >> 32);
        newCapacity = (int)l;
        if (newCapacity < this.capacity) {
            int oldCapacity = this.capacity;
            int[] oldPtr = this.nextPtr;
            long[] oldKeys = this.keys;
            int[] oldElements = this.elements;
            this.init(newCapacity);
            for (int i = 0; i < oldCapacity; ++i) {
                int pos = oldPtr[i];
                while (pos != -1) {
                    int index = pos - oldCapacity;
                    this.putQuick(oldKeys[index], oldElements[index]);
                    pos = oldPtr[pos];
                }
            }
        }
    }

    public boolean put(long key, int value) {
        int index;
        if (value == -123456789) {
            throw new IllegalStateException("value=" + value);
        }
        if (this.count == this.limit) {
            this.rehash();
        }
        int pos = HashMapLongInt.hash(key) % this.capacity;
        while (this.nextPtr[pos] != -1) {
            index = (pos = this.nextPtr[pos]) - this.capacity;
            if (this.keys[index] != key) continue;
            this.elements[index] = value;
            return true;
        }
        index = this.nextFree - this.capacity;
        this.nextPtr[pos] = this.nextFree;
        this.keys[index] = key;
        this.elements[index] = value;
        this.nextFree = this.nextPtr[this.nextFree];
        this.nextPtr[this.nextPtr[pos]] = -1;
        ++this.count;
        return false;
    }

    public int inc(long key) {
        int index;
        if (this.count == this.limit) {
            this.rehash();
        }
        int pos = HashMapLongInt.hash(key) % this.capacity;
        while (this.nextPtr[pos] != -1) {
            index = (pos = this.nextPtr[pos]) - this.capacity;
            if (this.keys[index] != key) continue;
            int n = index;
            int n2 = this.elements[n] + 1;
            this.elements[n] = n2;
            return n2;
        }
        index = this.nextFree - this.capacity;
        this.nextPtr[pos] = this.nextFree;
        this.keys[index] = key;
        this.elements[index] = 1;
        this.nextFree = this.nextPtr[this.nextFree];
        this.nextPtr[this.nextPtr[pos]] = -1;
        ++this.count;
        return 1;
    }

    protected void putQuick(long key, int value) {
        int pos = HashMapLongInt.hash(key) % this.capacity;
        while (this.nextPtr[pos] != -1) {
            pos = this.nextPtr[pos];
        }
        int index = this.nextFree - this.capacity;
        this.nextPtr[pos] = this.nextFree;
        this.keys[index] = key;
        this.elements[index] = value;
        this.nextFree = this.nextPtr[this.nextFree];
        this.nextPtr[this.nextPtr[pos]] = -1;
        ++this.count;
    }

    public int get(long key) {
        int pos = this.nextPtr[HashMapLongInt.hash(key) % this.capacity];
        while (pos != -1) {
            int index = pos - this.capacity;
            if (this.keys[index] == key) {
                return this.elements[index];
            }
            pos = this.nextPtr[pos];
        }
        return -123456789;
    }

    public int dec(long key) {
        int prevPos = HashMapLongInt.hash(key) % this.capacity;
        int pos = this.nextPtr[prevPos];
        while (pos != -1) {
            int index = pos - this.capacity;
            if (this.keys[index] == key) {
                int n = index;
                this.elements[n] = this.elements[n] - 1;
                if (this.elements[index] == 0) {
                    this.nextPtr[prevPos] = this.nextPtr[pos];
                    this.nextPtr[pos] = this.nextFree;
                    this.nextFree = pos;
                    --this.count;
                    return 0;
                }
                return this.elements[index];
            }
            prevPos = pos;
            pos = this.nextPtr[pos];
        }
        throw new IllegalStateException("Key " + key + " not found.");
    }

    public boolean remove(long key) {
        int prevPos = HashMapLongInt.hash(key) % this.capacity;
        int pos = this.nextPtr[prevPos];
        while (pos != -1) {
            if (this.keys[pos - this.capacity] == key) {
                this.nextPtr[prevPos] = this.nextPtr[pos];
                this.nextPtr[pos] = this.nextFree;
                this.nextFree = pos;
                --this.count;
                return true;
            }
            prevPos = pos;
            pos = this.nextPtr[pos];
        }
        return false;
    }

    protected void init(int initialCapacity) {
        int i;
        if (this.growStep > 17) {
            this.capacity = (int)PrimeGenerator.getClosestPrime(initialCapacity);
        } else {
            long l = PrimeGenerator.getClosestPrime(initialCapacity, this.simplIndex);
            this.simplIndex = (int)(l >> 32) + this.growSimpl;
            this.capacity = (int)l;
        }
        this.limit = (int)((float)this.capacity * this.loadFactor);
        this.nextPtr = new int[this.capacity + this.limit];
        for (i = 0; i < this.capacity; ++i) {
            this.nextPtr[i] = -1;
        }
        i = this.capacity;
        while (i < this.nextPtr.length) {
            this.nextPtr[i++] = i;
        }
        this.keys = new long[this.limit];
        this.elements = new int[this.limit];
        this.nextFree = this.capacity;
        this.count = 0;
    }

    protected void rehash() {
        long[] oldKeys = this.keys;
        int[] oldElements = this.elements;
        this.init(this.capacity * this.growStep);
        for (int i = 0; i < oldKeys.length; ++i) {
            this.putQuick(oldKeys[i], oldElements[i]);
        }
    }

    public String toString() {
        int c = 0;
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        for (int i = 0; i < this.capacity; ++i) {
            int pos = this.nextPtr[i];
            while (pos != -1) {
                int index = pos - this.capacity;
                buf.append(this.keys[index] + "=" + this.elements[index]);
                if (++c < this.count) {
                    buf.append(", ");
                }
                pos = this.nextPtr[pos];
            }
        }
        buf.append("}");
        return buf.toString();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        for (int i = 0; i < this.capacity; ++i) {
            int pos = this.nextPtr[i];
            while (pos != -1) {
                int index = pos - this.capacity;
                stream.writeLong(this.keys[index]);
                stream.writeInt(this.elements[index]);
                pos = this.nextPtr[pos];
            }
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int i;
        stream.defaultReadObject();
        this.nextPtr = new int[this.capacity + this.limit];
        for (i = 0; i < this.capacity; ++i) {
            this.nextPtr[i] = -1;
        }
        i = this.capacity;
        while (i < this.nextPtr.length) {
            this.nextPtr[i++] = i;
        }
        this.keys = new long[this.limit];
        this.elements = new int[this.limit];
        this.nextFree = this.capacity;
        int size = this.count;
        this.count = 0;
        for (int i2 = 0; i2 < size; ++i2) {
            this.putQuick(stream.readLong(), stream.readInt());
        }
    }

    public static int hash(long key) {
        return (int)(key & Integer.MAX_VALUE);
    }
}

