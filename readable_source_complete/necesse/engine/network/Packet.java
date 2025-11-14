/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import java.nio.ByteBuffer;
import java.util.Arrays;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;

public class Packet {
    public static final int CHUNK_SIZE = 512;
    public static final int BYTE_SIZE = 1;
    public static final int SHORT_SIZE = 2;
    public static final int INT_SIZE = 4;
    public static final int LONG_SIZE = 8;
    public static final int FLOAT_SIZE = 4;
    public static final int DOUBLE_SIZE = 8;
    public static final int CHAR_SIZE = 2;
    public static final int BOOLEAN_SIZE = 1;
    private int size;
    private byte[] data;
    private ByteBuffer buffer;

    public Packet() {
        this.data = new byte[0];
        this.buffer = ByteBuffer.wrap(this.data);
    }

    public Packet(byte[] data) {
        this.data = data;
        this.buffer = ByteBuffer.wrap(this.data);
        this.size = data.length;
    }

    public final void wrap(Packet packet) {
        this.data = packet.data;
        this.buffer = packet.buffer;
        this.size = packet.size;
    }

    public final void ensureCapacity(int minCapacity) {
        if (this.data.length < minCapacity) {
            int newCapacity;
            for (newCapacity = this.data.length; newCapacity < minCapacity; newCapacity += 512) {
            }
            this.data = Arrays.copyOf(this.data, newCapacity);
            this.buffer = ByteBuffer.wrap(this.data);
        }
    }

    public final int getCapacity() {
        return this.data.length;
    }

    private void ensureCapacityAndSetSize(int newSize) {
        this.ensureCapacity(newSize);
        this.size = Math.max(this.size, newSize);
    }

    public final Packet putByte(int offset, byte data) {
        this.ensureCapacityAndSetSize(offset + 1);
        this.buffer.put(offset, data);
        return this;
    }

    public final Packet putByteUnsigned(int offset, int data) {
        return this.putByte(offset, (byte)data);
    }

    public final byte getByte(int offset) {
        if (offset + 1 > this.data.length) {
            return 0;
        }
        return this.buffer.get(offset);
    }

    public final int getByteUnsigned(int offset) {
        return this.getByte(offset) & 0xFF;
    }

    public final Packet putShort(int offset, short data) {
        this.ensureCapacityAndSetSize(offset + 2);
        this.buffer.putShort(offset, data);
        return this;
    }

    public final Packet putShortUnsigned(int offset, int data) {
        return this.putShort(offset, (short)data);
    }

    public final short getShort(int offset) {
        if (offset + 2 > this.data.length) {
            return 0;
        }
        return this.buffer.getShort(offset);
    }

    public final int getShortUnsigned(int offset) {
        return this.getShort(offset) & 0xFFFF;
    }

    public final Packet putInt(int offset, int data) {
        this.ensureCapacityAndSetSize(offset + 4);
        this.buffer.putInt(offset, data);
        return this;
    }

    public final int getInt(int offset) {
        if (offset + 4 > this.data.length) {
            return 0;
        }
        return this.buffer.getInt(offset);
    }

    public final Packet putIntUnsigned(int offset, long data) {
        return this.putInt(offset, (int)data);
    }

    public final long getIntUnsigned(int offset, long data) {
        return Integer.toUnsignedLong(this.getInt(offset));
    }

    public final Packet putLong(int offset, long data) {
        this.ensureCapacityAndSetSize(offset + 8);
        this.buffer.putLong(offset, data);
        return this;
    }

    public final long getLong(int offset) {
        if (offset + 8 > this.data.length) {
            return 0L;
        }
        return this.buffer.getLong(offset);
    }

    public final Packet putFloat(int offset, float data) {
        this.ensureCapacityAndSetSize(offset + 4);
        this.buffer.putFloat(offset, data);
        return this;
    }

    public final float getFloat(int offset) {
        if (offset + 4 > this.data.length) {
            return 0.0f;
        }
        return this.buffer.getFloat(offset);
    }

    public final Packet putDouble(int offset, double data) {
        this.ensureCapacityAndSetSize(offset + 8);
        this.buffer.putDouble(offset, data);
        return this;
    }

    public final double getDouble(int offset) {
        if (offset + 8 > this.data.length) {
            return 0.0;
        }
        return this.buffer.getDouble(offset);
    }

    public final Packet putChar(int offset, char data) {
        this.ensureCapacityAndSetSize(offset + 2);
        this.buffer.putChar(offset, data);
        return this;
    }

    public final char getChar(int offset) {
        if (offset + 2 > this.data.length) {
            return '\u0000';
        }
        return this.buffer.getChar(offset);
    }

    public final Packet putBoolean(int offset, int bit, boolean data) {
        this.ensureCapacityAndSetSize(offset + 1);
        if (bit > 7 || bit < 0) {
            return this;
        }
        this.buffer.put(offset, GameMath.setBit(this.getByte(offset), bit, data));
        return this;
    }

    public final boolean getBoolean(int offset, int bit) {
        if (offset + 1 > this.data.length || bit > 7 || bit < 0) {
            return false;
        }
        return GameMath.getBit(this.getByte(offset), bit);
    }

    public final Packet putString(int offset, String data) {
        this.ensureCapacityAndSetSize(offset + data.length() * 2);
        char[] array = data.toCharArray();
        for (int i = 0; i < array.length; ++i) {
            this.buffer.putChar(offset + i * 2, array[i]);
        }
        return this;
    }

    public final String getString(int offset, int length) {
        char[] stringArray = new char[length];
        for (int i = 0; i < length; ++i) {
            stringArray[i] = this.getChar(offset + i * 2);
        }
        return new String(stringArray);
    }

    public final Packet putBytes(int offset, byte[] bytes) {
        this.ensureCapacityAndSetSize(offset + bytes.length);
        System.arraycopy(bytes, 0, this.data, offset, bytes.length);
        return this;
    }

    public final byte[] getBytes(int offset, int length) {
        byte[] out = new byte[length];
        System.arraycopy(this.data, offset, out, 0, length);
        return out;
    }

    public final Packet putContentPacket(int offset, Packet packet) {
        this.ensureCapacityAndSetSize(offset + packet.getSize());
        if (packet.getSize() >= 0) {
            System.arraycopy(packet.data, 0, this.data, offset, packet.getSize());
        }
        return this;
    }

    public final Packet getContentPacket(int offset, int size) {
        if (offset > this.data.length) {
            offset = this.data.length;
        }
        if (offset + size > this.data.length) {
            size = this.data.length - offset;
        }
        byte[] bytes = Arrays.copyOfRange(this.data, offset, offset + size);
        return new Packet(bytes);
    }

    public final byte[] getPacketData() {
        return Arrays.copyOf(this.data, this.size);
    }

    public final int getSize() {
        return this.size;
    }

    public final void printBitContent() {
        System.out.println("Packet size: " + this.getSize());
        for (int i = 0; i < this.getSize(); ++i) {
            StringBuilder s = new StringBuilder("Index " + i + ": ");
            for (int bit = 7; bit >= 0; --bit) {
                s.append(this.getBoolean(i, bit) ? "1" : "0");
            }
            System.out.println(s);
        }
    }

    protected Class<? extends Packet> getPacketClass() {
        return this.getClass();
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        System.out.println("Server received unknown packet: " + this.getPacketClass().getSimpleName());
    }

    public void processClient(NetworkPacket packet, Client client) {
        System.out.println("Client received unknown packet: " + this.getPacketClass().getSimpleName());
    }
}

