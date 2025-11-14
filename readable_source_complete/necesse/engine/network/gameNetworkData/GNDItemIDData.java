/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public abstract class GNDItemIDData<T>
extends GNDItem.GNDPrimitive {
    protected int id;

    public GNDItemIDData(int id) {
        this.id = id;
    }

    public GNDItemIDData(String stringID) {
        this.id = stringID == null ? -1 : this.getID(stringID);
    }

    public GNDItemIDData(T item) {
        this.id = item == null ? -1 : this.getID(item);
    }

    public GNDItemIDData(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemIDData(LoadData data) {
        String stringID = data.getUnsafeString("value", null);
        this.id = stringID == null ? -1 : this.getID(stringID);
    }

    protected abstract String getStringID(int var1);

    protected abstract int getID(String var1);

    protected abstract int getID(T var1);

    public String getItemStringID() {
        if (this.id == -1) {
            return null;
        }
        return this.getStringID(this.id);
    }

    @Override
    public String toString() {
        return this.getStringID(this.id);
    }

    @Override
    public boolean isDefault() {
        return this.id == -1;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItem.GNDPrimitive) {
            return this.getInt() == ((GNDItem.GNDPrimitive)item).getInt();
        }
        return false;
    }

    @Override
    public void addSaveData(SaveData data) {
        if (this.id != -1) {
            data.addUnsafeString("value", this.getStringID(this.id));
        }
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextInt(this.id);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.id = reader.getNextInt();
    }

    @Override
    public boolean getBoolean() {
        return this.id != -1;
    }

    @Override
    public byte getByte() {
        return (byte)this.id;
    }

    @Override
    public short getShort() {
        return (short)this.id;
    }

    @Override
    public int getInt() {
        return this.id;
    }

    @Override
    public long getLong() {
        return this.id;
    }

    @Override
    public float getFloat() {
        return this.id;
    }

    @Override
    public double getDouble() {
        return this.id;
    }

    public static int getItemID(GNDItemMap gndData, String keyName) {
        return gndData.getInt(keyName, -1);
    }
}

