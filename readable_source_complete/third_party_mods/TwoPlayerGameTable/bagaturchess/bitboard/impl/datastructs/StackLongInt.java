/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StackLongInt {
    protected static final int LAST = -1;
    public static final int NO_VALUE = -1;
    protected int capacity;
    protected transient int nextFree;
    protected transient long[] keys;
    protected transient int[] elements;
    protected transient int[] nextPtr;
    protected int count;

    public StackLongInt(int initialCapacity) {
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
        int pos = this.nextPtr[StackLongInt.hash(key) % this.capacity];
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
        StackLongInt result = null;
        try {
            result = (StackLongInt)super.clone();
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
        if (!(object instanceof StackLongInt)) {
            return false;
        }
        StackLongInt t = (StackLongInt)object;
        if (t.count != this.count) {
            return false;
        }
        for (int i = 0; i < this.capacity; ++i) {
            int pos = this.nextPtr[i];
            while (pos != -1) {
                int index = pos - this.capacity;
                int temp = t.get(this.keys[index]);
                if (temp == -1) {
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

    public int inc(long key) {
        int index;
        if (this.count == this.elements.length) {
            throw new IllegalStateException("Not enough initial space.");
        }
        int pos = StackLongInt.hash(key) % this.capacity;
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

    public int get(long key) {
        int pos = this.nextPtr[StackLongInt.hash(key) % this.capacity];
        while (pos != -1) {
            int index = pos - this.capacity;
            if (index >= this.capacity) {
                return -1;
            }
            if (this.keys[index] == key) {
                return this.elements[index];
            }
            pos = this.nextPtr[pos];
        }
        return -1;
    }

    public int dec(long key) {
        int prevPos = StackLongInt.hash(key) % this.capacity;
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
                }
                return this.elements[index];
            }
            prevPos = pos;
            pos = this.nextPtr[pos];
        }
        return -1;
    }

    protected void init(int initialCapacity) {
        int i;
        this.capacity = initialCapacity;
        this.nextPtr = new int[2 * this.capacity];
        for (i = 0; i < this.capacity; ++i) {
            this.nextPtr[i] = -1;
        }
        i = this.capacity;
        while (i < this.nextPtr.length) {
            this.nextPtr[i++] = i;
        }
        this.keys = new long[this.capacity];
        this.elements = new int[this.capacity];
        this.nextFree = this.capacity;
        this.count = 0;
    }

    public static int hash(long key) {
        return (int)(key & Integer.MAX_VALUE);
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
        this.nextPtr = new int[this.capacity];
        for (i = 0; i < this.capacity; ++i) {
            this.nextPtr[i] = -1;
        }
        i = this.capacity;
        while (i < this.nextPtr.length) {
            this.nextPtr[i++] = i;
        }
        this.keys = new long[this.capacity];
        this.elements = new int[this.capacity];
        this.nextFree = this.capacity;
        int size = this.count;
        this.count = 0;
        for (int i2 = 0; i2 < size; ++i2) {
            this.putQuick(stream.readLong(), stream.readInt());
        }
    }

    protected void putQuick(long key, int value) {
        int pos = StackLongInt.hash(key) % this.capacity;
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
}

