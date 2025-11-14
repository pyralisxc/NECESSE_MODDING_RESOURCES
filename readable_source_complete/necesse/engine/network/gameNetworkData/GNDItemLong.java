/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemLong
extends GNDItem.GNDPrimitive {
    private long data;

    public GNDItemLong(long data) {
        this.data = data;
    }

    public GNDItemLong(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemLong(LoadData data) {
        this.data = data.getLong("value", 0L);
    }

    @Override
    public String toString() {
        return Long.toString(this.data);
    }

    @Override
    public boolean isDefault() {
        return this.data == 0L;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItem.GNDPrimitive) {
            return this.getLong() == ((GNDItem.GNDPrimitive)item).getLong();
        }
        return false;
    }

    @Override
    public GNDItemLong copy() {
        return new GNDItemLong(this.data);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addLong("value", this.data);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextLong(this.data);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.data = reader.getNextLong();
    }

    @Override
    public boolean getBoolean() {
        return this.data != 0L;
    }

    @Override
    public byte getByte() {
        return (byte)this.data;
    }

    @Override
    public short getShort() {
        return (short)this.data;
    }

    @Override
    public int getInt() {
        return (int)this.data;
    }

    @Override
    public long getLong() {
        return this.data;
    }

    @Override
    public float getFloat() {
        return this.data;
    }

    @Override
    public double getDouble() {
        return this.data;
    }
}

