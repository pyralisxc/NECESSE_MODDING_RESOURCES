/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public class ShippingChestObjectEntity
extends InventoryObjectEntity {
    public int startMissionWhenCarryingAtLeastStacks = 20;
    public LevelStorage nonSettlementStorage;

    public ShippingChestObjectEntity(Level level, int x, int y) {
        super(level, x, y, 20);
        this.nonSettlementStorage = new LevelStorage(level, x, y){

            @Override
            public InventoryRange getInventoryRange() {
                return new InventoryRange(ShippingChestObjectEntity.this.inventory);
            }

            @Override
            public ItemCategoriesFilter getFilter() {
                return null;
            }

            @Override
            public GameMessage getInventoryName() {
                return ShippingChestObjectEntity.this.getInventoryName();
            }
        };
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("startMissionWhenCarryingAtLeastStacks", this.startMissionWhenCarryingAtLeastStacks);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.startMissionWhenCarryingAtLeastStacks = save.getInt("startMissionWhenCarryingAtLeastStacks", this.startMissionWhenCarryingAtLeastStacks);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextShortUnsigned(this.startMissionWhenCarryingAtLeastStacks);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.startMissionWhenCarryingAtLeastStacks = reader.getNextShortUnsigned();
    }

    @Override
    public void setupDefaultSettlementStorage(SettlementInventory inventory) {
        super.setupDefaultSettlementStorage(inventory);
        inventory.filter.master.setAllowed(false);
        inventory.priority = SettlementInventory.Priority.LAST.priorityValue;
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        return item == null || !item.item.getStringID().equals("coin");
    }
}

