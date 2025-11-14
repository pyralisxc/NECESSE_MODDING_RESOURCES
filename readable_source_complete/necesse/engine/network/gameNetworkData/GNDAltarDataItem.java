/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.incursion.AltarData;

public class GNDAltarDataItem
extends GNDItem {
    public AltarData altarData;

    public GNDAltarDataItem(AltarData altarData) {
        this.altarData = altarData;
    }

    public GNDAltarDataItem(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDAltarDataItem(LoadData data) {
        try {
            LoadData value = data.getFirstLoadDataByName("value");
            if (value != null) {
                this.altarData = new AltarData();
                this.altarData.applyLoadData(value);
            }
        }
        catch (Exception e) {
            this.altarData = null;
        }
    }

    @Override
    public String toString() {
        if (this.altarData == null) {
            return "NULL";
        }
        return this.altarData.getDebugString();
    }

    @Override
    public boolean isDefault() {
        return this.altarData == null;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDAltarDataItem) {
            GNDAltarDataItem other = (GNDAltarDataItem)item;
            if (this.altarData == other.altarData) {
                return true;
            }
            if (this.altarData != null && other.altarData != null) {
                return this.altarData.isSameAltarData(other.altarData);
            }
            return false;
        }
        return false;
    }

    @Override
    public GNDItem copy() {
        return new GNDAltarDataItem(this.altarData == null ? null : this.altarData.makeCopy());
    }

    @Override
    public void addSaveData(SaveData data) {
        if (this.altarData != null) {
            SaveData itemData = new SaveData("value");
            this.altarData.addSaveData(itemData);
            data.addSaveData(itemData);
        }
    }

    @Override
    public void writePacket(PacketWriter writer) {
        if (this.altarData != null) {
            writer.putNextBoolean(true);
            this.altarData.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void readPacket(PacketReader reader) {
        if (reader.getNextBoolean()) {
            this.altarData = new AltarData();
            this.altarData.applyPacket(reader);
        } else {
            this.altarData = null;
        }
    }
}

