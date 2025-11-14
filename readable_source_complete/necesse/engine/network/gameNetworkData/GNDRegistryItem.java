/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public abstract class GNDRegistryItem
extends GNDItem {
    protected int offsetID;

    public GNDRegistryItem(String stringID) {
        try {
            this.setRegistryID(this.toID(stringID));
        }
        catch (Exception e) {
            this.offsetID = 0;
        }
    }

    public GNDRegistryItem(int id) {
        this.setRegistryID(id);
    }

    public GNDRegistryItem(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDRegistryItem(LoadData data) {
        if (data.hasLoadDataByName("value")) {
            this.setRegistryID(this.toID(data.getUnsafeString("value")));
        } else {
            this.offsetID = 0;
        }
    }

    public int getRegistryID() {
        return this.offsetID - 1;
    }

    public void setRegistryID(int id) {
        this.offsetID = Math.max(0, id + 1);
    }

    @Override
    public String toString() {
        if (this.offsetID <= 0) {
            return "null";
        }
        return this.toStringID(this.getRegistryID());
    }

    @Override
    public boolean isDefault() {
        return this.offsetID <= 0;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDRegistryItem) {
            return this.offsetID == ((GNDRegistryItem)item).offsetID;
        }
        if (item instanceof GNDItemString) {
            return this.toString().equals(item.toString());
        }
        if (item instanceof GNDItem.GNDPrimitive) {
            return this.offsetID == Math.max(0, ((GNDItem.GNDPrimitive)item).getInt() + 1);
        }
        return false;
    }

    @Override
    public void addSaveData(SaveData data) {
        block3: {
            try {
                if (this.offsetID <= 0) {
                    throw new RuntimeException();
                }
                data.addUnsafeString("value", this.toStringID(this.getRegistryID()));
            }
            catch (Exception e) {
                String errorStringID = this.getDefaultStringID();
                if (errorStringID == null) break block3;
                data.addUnsafeString("value", errorStringID);
            }
        }
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.offsetID);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.offsetID = reader.getNextShortUnsigned();
    }

    protected abstract int toID(String var1);

    protected abstract String toStringID(int var1);

    protected String getDefaultStringID() {
        return null;
    }
}

