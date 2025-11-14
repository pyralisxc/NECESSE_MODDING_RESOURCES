/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;

public class GNDItemInventoryItem
extends GNDItem {
    public InventoryItem invItem;

    public GNDItemInventoryItem(InventoryItem invItem) {
        this.invItem = invItem;
    }

    public GNDItemInventoryItem(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemInventoryItem(LoadData data) {
        this.invItem = InventoryItem.fromLoadData(data.getFirstLoadDataByName("value"));
    }

    @Override
    public String toString() {
        if (this.invItem == null) {
            return "NULL";
        }
        return this.invItem.item.getStringID() + ":" + this.invItem.getAmount() + ":" + this.invItem.isLocked() + ":" + this.invItem.isNew() + ":" + this.invItem.getGndData().toString();
    }

    @Override
    public boolean isDefault() {
        return this.invItem == null;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItemInventoryItem) {
            GNDItemInventoryItem other = (GNDItemInventoryItem)item;
            if (this.invItem == other.invItem) {
                return true;
            }
            if (this.invItem != null) {
                return this.invItem.equals(null, other.invItem, "equals");
            }
            return false;
        }
        return false;
    }

    @Override
    public GNDItem copy() {
        return new GNDItemInventoryItem(this.invItem == null ? null : this.invItem.copy());
    }

    @Override
    public void addSaveData(SaveData data) {
        if (this.invItem != null) {
            SaveData itemData = new SaveData("value");
            this.invItem.addSaveData(itemData);
            data.addSaveData(itemData);
        }
    }

    @Override
    public void writePacket(PacketWriter writer) {
        InventoryItem.addPacketContent(this.invItem, writer);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.invItem = InventoryItem.fromContentPacket(reader);
    }
}

