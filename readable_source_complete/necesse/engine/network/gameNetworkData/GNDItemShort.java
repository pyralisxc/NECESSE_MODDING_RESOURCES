/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemShort
extends GNDItem.GNDPrimitive {
    private short data;

    public GNDItemShort(short data) {
        this.data = data;
    }

    public GNDItemShort(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemShort(LoadData data) {
        this.data = data.getShort("value", (short)0);
    }

    @Override
    public boolean isDefault() {
        return this.data == 0;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItem.GNDPrimitive) {
            return this.getLong() == ((GNDItem.GNDPrimitive)item).getLong();
        }
        return false;
    }

    @Override
    public GNDItemShort copy() {
        return new GNDItemShort(this.data);
    }

    @Override
    public String toString() {
        return Short.toString(this.data);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addShort("value", this.data);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextShort(this.data);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.data = reader.getNextShort();
    }

    @Override
    public boolean getBoolean() {
        return this.data != 0;
    }

    @Override
    public byte getByte() {
        return (byte)this.data;
    }

    @Override
    public short getShort() {
        return this.data;
    }

    @Override
    public int getInt() {
        return this.data;
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

