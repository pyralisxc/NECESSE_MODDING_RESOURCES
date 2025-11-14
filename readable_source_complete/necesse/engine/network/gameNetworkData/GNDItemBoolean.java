/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemBoolean
extends GNDItem.GNDPrimitive {
    private boolean data;

    public GNDItemBoolean(boolean data) {
        this.data = data;
    }

    public GNDItemBoolean(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemBoolean(LoadData data) {
        this.data = data.getBoolean("value", false);
    }

    @Override
    public String toString() {
        return Boolean.toString(this.data);
    }

    @Override
    public boolean isDefault() {
        return !this.data;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItem.GNDPrimitive) {
            return this.getBoolean() == ((GNDItem.GNDPrimitive)item).getBoolean();
        }
        return false;
    }

    @Override
    public GNDItemBoolean copy() {
        return new GNDItemBoolean(this.data);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addBoolean("value", this.data);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextBoolean(this.data);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.data = reader.getNextBoolean();
    }

    @Override
    public boolean getBoolean() {
        return this.data;
    }

    @Override
    public byte getByte() {
        return this.data ? (byte)1 : 0;
    }

    @Override
    public short getShort() {
        return this.data ? (short)1 : 0;
    }

    @Override
    public int getInt() {
        return this.data ? 1 : 0;
    }

    @Override
    public long getLong() {
        return this.data ? 1L : 0L;
    }

    @Override
    public float getFloat() {
        return this.data ? 1.0f : 0.0f;
    }

    @Override
    public double getDouble() {
        return this.data ? 1.0 : 0.0;
    }
}

