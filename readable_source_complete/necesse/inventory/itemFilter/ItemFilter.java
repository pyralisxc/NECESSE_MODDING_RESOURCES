/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.itemFilter;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;

public class ItemFilter {
    public final int itemID;
    public final int maxAmount;

    public ItemFilter(int itemID, int maxAmount) {
        this.itemID = itemID;
        this.maxAmount = maxAmount;
    }

    public ItemFilter(int itemID) {
        this(itemID, Integer.MAX_VALUE);
    }

    public ItemFilter(LoadData save) throws LoadDataException {
        String itemStringID = save.getUnsafeString("itemStringID", null, false);
        if (itemStringID == null) {
            throw new LoadDataException("Could not find itemStringID");
        }
        itemStringID = VersionMigration.tryFixStringID(itemStringID, VersionMigration.oldItemStringIDs);
        this.itemID = ItemRegistry.getItemID(itemStringID);
        if (this.itemID == -1) {
            throw new LoadDataException("Could not find item with stringID " + itemStringID);
        }
        this.maxAmount = save.getInt("maxAmount", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("itemStringID", ItemRegistry.getItemStringID(this.itemID));
        if (this.maxAmount != Integer.MAX_VALUE) {
            save.addInt("maxAmount", this.maxAmount);
        }
    }

    public ItemFilter(PacketReader reader) {
        this.itemID = reader.getNextShortUnsigned();
        this.maxAmount = reader.getNextInt();
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.itemID);
        writer.putNextInt(this.maxAmount);
    }

    public int getAddAmount(Level level, InventoryItem item, InventoryRange range) {
        if (this.maxAmount == Integer.MAX_VALUE) {
            return item.getAmount();
        }
        int amount = range.inventory.getAmount(level, null, ItemRegistry.getItem(this.itemID), range.startSlot, range.endSlot, "filteramount");
        return GameMath.limit(this.maxAmount - amount, 0, item.getAmount());
    }

    public int getRemoveAmount(Level level, InventoryItem item, InventoryRange range) {
        if (this.maxAmount == Integer.MAX_VALUE) {
            return 0;
        }
        int amount = range.inventory.getAmount(level, null, ItemRegistry.getItem(this.itemID), range.startSlot, range.endSlot, "filteramount");
        return Math.max(0, amount - this.maxAmount);
    }

    public boolean matchesItem(InventoryItem item) {
        return item.item.getID() == this.itemID;
    }

    public boolean isSameFilter(ItemFilter other) {
        return this.itemID == other.itemID;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ItemFilter) {
            return this.isSameFilter((ItemFilter)obj);
        }
        return super.equals(obj);
    }
}

