/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.incursion.IncursionData;

public class GNDIncursionDataItem
extends GNDItem {
    public IncursionData incursionData;

    public GNDIncursionDataItem(IncursionData incursionData) {
        this.incursionData = incursionData;
    }

    public GNDIncursionDataItem(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDIncursionDataItem(LoadData data) {
        try {
            this.incursionData = IncursionData.fromLoadData(data.getFirstLoadDataByName("value"));
        }
        catch (Exception e) {
            this.incursionData = null;
        }
    }

    @Override
    public String toString() {
        if (this.incursionData == null) {
            return "NULL";
        }
        return this.incursionData.getStringID() + ":" + this.incursionData;
    }

    @Override
    public boolean isDefault() {
        return this.incursionData == null;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDIncursionDataItem) {
            GNDIncursionDataItem other = (GNDIncursionDataItem)item;
            if (this.incursionData == other.incursionData) {
                return true;
            }
            if (this.incursionData != null && other.incursionData != null) {
                return this.incursionData.isSameIncursion(other.incursionData);
            }
            return false;
        }
        return false;
    }

    @Override
    public GNDItem copy() {
        return new GNDIncursionDataItem(this.incursionData == null ? null : IncursionData.makeCopy(this.incursionData));
    }

    @Override
    public void addSaveData(SaveData data) {
        if (this.incursionData != null) {
            SaveData itemData = new SaveData("value");
            this.incursionData.addSaveData(itemData);
            data.addSaveData(itemData);
        }
    }

    @Override
    public void writePacket(PacketWriter writer) {
        IncursionData.writePacket(this.incursionData, writer);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.incursionData = IncursionData.fromPacket(reader);
    }
}

