/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.level.maps.Level;

public class UpgradeStationObjectEntity
extends ObjectEntity
implements OEInventory {
    public Inventory inventory = new Inventory(1);

    public UpgradeStationObjectEntity(Level level, int x, int y) {
        super(level, "upgradestation", x, y);
        this.inventory.filter = (slot, item) -> {
            if (item == null) {
                return true;
            }
            return item.item instanceof UpgradableItem && ((UpgradableItem)((Object)item.item)).getCanBeUpgradedError(item) == null;
        };
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSaveData(InventorySave.getSave(this.inventory, "INVENTORY"));
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.inventory.override(InventorySave.loadSave(save.getFirstLoadDataByName("INVENTORY")));
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.inventory.writeContent(writer);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.inventory.override(Inventory.getInventory(reader));
    }

    @Override
    public ArrayList<InventoryItem> getDroppedItems() {
        ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.isSlotClear(i)) continue;
            list.add(this.inventory.getItem(i));
        }
        return list;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.inventory.tickItems(this);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.inventory.tickItems(this);
        this.serverTickInventorySync(this.getLevel().getServer(), this);
    }

    @Override
    public void markClean() {
        super.markClean();
        this.inventory.clean();
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public GameMessage getInventoryName() {
        return this.getObject().getLocalization();
    }

    @Override
    public boolean canSetInventoryName() {
        return false;
    }

    @Override
    public boolean canQuickStackInventory() {
        return false;
    }

    @Override
    public boolean canRestockInventory() {
        return false;
    }

    @Override
    public boolean canSortInventory() {
        return false;
    }

    @Override
    public boolean canUseForNearbyCrafting() {
        return false;
    }

    @Override
    public InventoryRange getSettlementStorage() {
        return null;
    }
}

