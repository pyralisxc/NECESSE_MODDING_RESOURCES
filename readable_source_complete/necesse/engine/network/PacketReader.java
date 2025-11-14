/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketIterator;
import necesse.engine.util.GameMath;

public class PacketReader
extends PacketIterator {
    public PacketReader(Packet packet, int startIndex) {
        super(packet, startIndex);
    }

    public PacketReader(Packet packet) {
        super(packet);
    }

    public PacketReader(PacketIterator copy) {
        super(copy);
    }

    public boolean getNextBoolean() {
        return this.packet.getBoolean(this.getNextBitIndex(), this.getNextBit());
    }

    public int getNextBitValue(int bits) {
        int value = 0;
        for (int i = 0; i < bits; ++i) {
            value = GameMath.setBit(value, i, this.getNextBoolean());
        }
        return value;
    }

    public int getNextMaxValue(int maxValue) {
        return this.getNextBitValue((int)(Math.log(maxValue) / Math.log(2.0) + 1.0));
    }

    public byte getNextByte() {
        byte out = this.packet.getByte(this.getNextIndex());
        this.addIndex(1);
        return out;
    }

    public int getNextByteUnsigned() {
        int out = this.packet.getByteUnsigned(this.getNextIndex());
        this.addIndex(1);
        return out;
    }

    public short getNextShort() {
        short out = this.packet.getShort(this.getNextIndex());
        this.addIndex(2);
        return out;
    }

    public int getNextShortUnsigned() {
        int out = this.packet.getShortUnsigned(this.getNextIndex());
        this.addIndex(2);
        return out;
    }

    public int getNextInt() {
        int out = this.packet.getInt(this.getNextIndex());
        this.addIndex(4);
        return out;
    }

    public float getNextFloat() {
        float out = this.packet.getFloat(this.getNextIndex());
        this.addIndex(4);
        return out;
    }

    public long getNextLong() {
        long out = this.packet.getLong(this.getNextIndex());
        this.addIndex(8);
        return out;
    }

    public double getNextDouble() {
        double out = this.packet.getDouble(this.getNextIndex());
        this.addIndex(8);
        return out;
    }

    public <T extends Enum<T>> T getNextEnum(Class<T> enumClass) {
        Enum[] constants = (Enum[])enumClass.getEnumConstants();
        return (T)constants[this.getNextMaxValue(constants.length)];
    }

    private String getNextString(int length) {
        String out = this.packet.getString(this.getNextIndex(), length);
        this.addIndex(length * 2);
        return out;
    }

    public String getNextString() {
        int length = this.getNextShortUnsigned();
        return this.getNextString(length);
    }

    public String getNextStringLong() {
        int length = this.getNextInt();
        return this.getNextString(length);
    }

    public byte[] getNextBytes(int length) {
        byte[] out = this.packet.getBytes(this.getNextIndex(), length);
        this.addIndex(length);
        return out;
    }

    public int[] getNextBytesUnsigned(int length) {
        int[] out = new int[length];
        for (int i = 0; i < length; ++i) {
            out[i] = this.getNextByteUnsigned();
        }
        return out;
    }

    public short[] getNextShorts(int length) {
        short[] out = new short[length];
        for (int i = 0; i < length; ++i) {
            out[i] = this.getNextShort();
        }
        return out;
    }

    public int[] getNextShortsUnsigned(int length) {
        int[] out = new int[length];
        for (int i = 0; i < length; ++i) {
            out[i] = this.getNextShortUnsigned();
        }
        return out;
    }

    public int[] getNextInts(int length) {
        int[] out = new int[length];
        for (int i = 0; i < length; ++i) {
            out[i] = this.getNextInt();
        }
        return out;
    }

    public long[] getNextLongs(int length) {
        long[] out = new long[length];
        for (int i = 0; i < length; ++i) {
            out[i] = this.getNextLong();
        }
        return out;
    }

    public boolean[] getNextBooleans(int length) {
        boolean[] out = new boolean[length];
        for (int i = 0; i < length; ++i) {
            out[i] = this.getNextBoolean();
        }
        return out;
    }

    public byte[] getRemainingBytes() {
        int size = this.packet.getSize() - this.getNextIndex();
        return this.getNextBytes(size);
    }

    public Packet getRemainingBytesPacket() {
        return new Packet(this.getRemainingBytes());
    }

    public int getRemainingSize() {
        return this.packet.getSize() - this.getNextIndex();
    }

    private Packet getNextContentPacket(int size) {
        Packet out = this.packet.getContentPacket(this.getNextIndex(), size);
        this.addIndex(size);
        return out;
    }

    public Packet getNextContentPacket() {
        int size = this.getNextInt();
        return this.getNextContentPacket(size);
    }

    public <T, L extends Collection<T>> void assignNextCollection(L collection, Supplier<T> elementConstructor) {
        collection.clear();
        int size = this.getNextShortUnsigned();
        for (int i = 0; i < size; ++i) {
            collection.add(elementConstructor.get());
        }
    }

    public <T, L extends Collection<T>> L getNextCollection(IntFunction<L> collectionConstructor, Supplier<T> elementConstructor) {
        int size = this.getNextShortUnsigned();
        Collection list = (Collection)collectionConstructor.apply(size);
        for (int i = 0; i < size; ++i) {
            list.add(elementConstructor.get());
        }
        return (L)list;
    }
}

