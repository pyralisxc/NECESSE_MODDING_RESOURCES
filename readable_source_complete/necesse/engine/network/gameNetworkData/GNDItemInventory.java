/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class GNDItemInventory
extends GNDItem {
    public Inventory inventory;

    public GNDItemInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public GNDItemInventory(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemInventory(LoadData data) {
        this.inventory = InventorySave.loadSave(data.getFirstLoadDataByName("value"));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("inv[").append(this.inventory.getSize()).append("]{");
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.isSlotClear(i)) continue;
            s.append("[").append(i).append(":");
            s.append(this.toString(this.inventory.getItem(i)));
            s.append("]");
        }
        s.append("}");
        return s.toString();
    }

    private String toString(InventoryItem item) {
        return item.item.getStringID() + ":" + item.getAmount() + ":" + item.isLocked() + ":" + item.isNew() + ":" + item.getGndData().toString();
    }

    @Override
    public boolean isDefault() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getAmount(i) <= 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItemInventory) {
            GNDItemInventory other = (GNDItemInventory)item;
            if (this.inventory.getSize() != other.inventory.getSize()) {
                return false;
            }
            for (int i = 0; i < this.inventory.getSize(); ++i) {
                if (this.inventory.isSlotClear(i) != other.inventory.isSlotClear(i)) {
                    return false;
                }
                if (this.inventory.isSlotClear(i) || this.inventory.getItem(i).equals(null, other.inventory.getItem(i), "equals")) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public GNDItemInventory copy() {
        return new GNDItemInventory(Inventory.getInventory(this.inventory.getContentPacket()));
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addSaveData(InventorySave.getSave(this.inventory, "value"));
    }

    @Override
    public void writePacket(PacketWriter writer) {
        this.inventory.writeContent(writer);
    }

    @Override
    public void readPacket(PacketReader reader) {
        if (this.inventory == null) {
            this.inventory = Inventory.getInventory(reader);
        } else {
            this.inventory.override(Inventory.getInventory(reader), true, true);
        }
    }
}

