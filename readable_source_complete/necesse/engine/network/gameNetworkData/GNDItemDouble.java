/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemDouble
extends GNDItem.GNDPrimitive {
    private double data;

    public GNDItemDouble(double data) {
        this.data = data;
    }

    public GNDItemDouble(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemDouble(LoadData data) {
        this.data = data.getDouble("value", 0.0);
    }

    @Override
    public String toString() {
        return this.data + "d";
    }

    @Override
    public boolean isDefault() {
        return this.data == 0.0;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItem.GNDPrimitive) {
            return this.getDouble() == ((GNDItem.GNDPrimitive)item).getDouble();
        }
        return false;
    }

    @Override
    public GNDItemDouble copy() {
        return new GNDItemDouble(this.data);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addDouble("value", this.data);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextDouble(this.data);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.data = reader.getNextDouble();
    }

    @Override
    public boolean getBoolean() {
        return this.data != 0.0;
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
        return (long)this.data;
    }

    @Override
    public float getFloat() {
        return (float)this.data;
    }

    @Override
    public double getDouble() {
        return this.data;
    }
}

