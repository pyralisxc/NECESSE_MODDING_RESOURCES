/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import java.util.Collection;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketIterator;
import necesse.engine.util.GameMath;

public class PacketWriter
extends PacketIterator {
    public PacketWriter(Packet packet, int startIndex) {
        super(packet, startIndex);
    }

    public PacketWriter(Packet packet) {
        super(packet);
    }

    public PacketWriter(PacketIterator copy) {
        super(copy);
    }

    public PacketWriter putNextBoolean(boolean data) {
        this.packet.putBoolean(this.getNextBitIndex(), this.getNextBit(), data);
        return this;
    }

    public PacketWriter putNextBitValue(int value, int bits) {
        for (int i = 0; i < bits; ++i) {
            this.putNextBoolean(GameMath.getBit(value, i));
        }
        return this;
    }

    public PacketWriter putNextMaxValue(int value, int maxValue) {
        return this.putNextBitValue(value, (int)(Math.log(maxValue) / Math.log(2.0) + 1.0));
    }

    public PacketWriter putNextByte(byte data) {
        this.packet.putByte(this.getNextIndex(), data);
        this.addIndex(1);
        return this;
    }

    public PacketWriter putNextByteUnsigned(int data) {
        this.packet.putByteUnsigned(this.getNextIndex(), data);
        this.addIndex(1);
        return this;
    }

    public PacketWriter putNextShort(short data) {
        this.packet.putShort(this.getNextIndex(), data);
        this.addIndex(2);
        return this;
    }

    public PacketWriter putNextShortUnsigned(int data) {
        this.packet.putShortUnsigned(this.getNextIndex(), data);
        this.addIndex(2);
        return this;
    }

    public PacketWriter putNextInt(int data) {
        this.packet.putInt(this.getNextIndex(), data);
        this.addIndex(4);
        return this;
    }

    public PacketWriter putNextIntUnsigned(long data) {
        this.packet.putIntUnsigned(this.getNextIndex(), data);
        this.addIndex(4);
        return this;
    }

    public PacketWriter putNextFloat(float data) {
        this.packet.putFloat(this.getNextIndex(), data);
        this.addIndex(4);
        return this;
    }

    public PacketWriter putNextLong(long data) {
        this.packet.putLong(this.getNextIndex(), data);
        this.addIndex(8);
        return this;
    }

    public PacketWriter putNextDouble(double data) {
        this.packet.putDouble(this.getNextIndex(), data);
        this.addIndex(8);
        return this;
    }

    public PacketWriter putNextEnum(Enum data) {
        Enum[] constants = (Enum[])data.getClass().getEnumConstants();
        this.putNextMaxValue(data.ordinal(), constants.length);
        return this;
    }

    public PacketWriter putNextString(String data) {
        if (data.length() > 65535) {
            GameLog.warn.println("Tried to put string longer than 65535. Use PacketWriter.putNextStringLong for long strings.");
            data = data.substring(0, 65535);
        }
        this.putNextShortUnsigned(data.length());
        this.packet.putString(this.getNextIndex(), data);
        this.addIndex(data.length() * 2);
        return this;
    }

    public PacketWriter putNextStringLong(String data) {
        this.putNextInt(data.length());
        this.packet.putString(this.getNextIndex(), data);
        this.addIndex(data.length() * 2);
        return this;
    }

    public PacketWriter putNextBytes(byte[] bytes) {
        this.packet.putBytes(this.getNextIndex(), bytes);
        this.addIndex(bytes.length);
        return this;
    }

    public PacketWriter putNextBytesUnsigned(int[] values) {
        for (int value : values) {
            this.putNextByteUnsigned(value);
        }
        return this;
    }

    public PacketWriter putNextShorts(short[] values) {
        for (short value : values) {
            this.putNextShort(value);
        }
        return this;
    }

    public PacketWriter putNextShortsUnsigned(int[] values) {
        for (int value : values) {
            this.putNextShortUnsigned(value);
        }
        return this;
    }

    public PacketWriter putNextInts(int[] values) {
        for (int value : values) {
            this.putNextInt(value);
        }
        return this;
    }

    public PacketWriter putNextLongs(long[] values) {
        for (long value : values) {
            this.putNextLong(value);
        }
        return this;
    }

    public PacketWriter putNextBooleans(boolean[] values) {
        for (boolean value : values) {
            this.putNextBoolean(value);
        }
        return this;
    }

    public PacketWriter putNextContentPacket(Packet data) {
        this.putNextInt(data.getSize());
        this.packet.putContentPacket(this.getNextIndex(), data);
        this.addIndex(data.getSize());
        return this;
    }

    public <T> PacketWriter putNextCollection(Collection<T> list, Consumer<T> elementWriter) {
        this.putNextShortUnsigned(list.size());
        for (T element : list) {
            elementWriter.accept(element);
        }
        return this;
    }
}

