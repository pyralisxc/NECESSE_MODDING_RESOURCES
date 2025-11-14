/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemInt
extends GNDItem.GNDPrimitive {
    private int data;

    public GNDItemInt(int data) {
        this.data = data;
    }

    public GNDItemInt(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemInt(LoadData data) {
        this.data = data.getInt("value", 0);
    }

    @Override
    public String toString() {
        return Integer.toString(this.data);
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
    public GNDItemInt copy() {
        return new GNDItemInt(this.data);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addInt("value", this.data);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextInt(this.data);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.data = reader.getNextInt();
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
        return (short)this.data;
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

