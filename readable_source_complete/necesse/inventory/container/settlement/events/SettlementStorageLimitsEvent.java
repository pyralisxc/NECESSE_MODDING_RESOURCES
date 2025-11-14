/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementStorageLimitsEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int tileX;
    public final int tileY;
    public final boolean isItems;
    public final Item item;
    public final ItemCategoriesFilter.ItemLimits limits;
    public final ItemCategory category;
    public final int maxItems;

    public SettlementStorageLimitsEvent(ServerSettlementData data, int tileX, int tileY, Item item, ItemCategoriesFilter.ItemLimits limits) {
        this.settlementUniqueID = data.uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.isItems = true;
        this.limits = limits;
        this.item = item;
        this.category = null;
        this.maxItems = 0;
    }

    public SettlementStorageLimitsEvent(ServerSettlementData data, int tileX, int tileY, ItemCategory category, int maxItems) {
        this.settlementUniqueID = data.uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.isItems = false;
        this.item = null;
        this.limits = null;
        this.category = category;
        this.maxItems = maxItems;
    }

    public SettlementStorageLimitsEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.isItems = reader.getNextBoolean();
        if (this.isItems) {
            this.category = null;
            this.maxItems = 0;
            int itemID = reader.getNextShortUnsigned();
            this.limits = new ItemCategoriesFilter.ItemLimits();
            this.limits.readPacket(reader);
            this.item = ItemRegistry.getItem(itemID);
        } else {
            this.item = null;
            this.limits = null;
            this.category = ItemCategory.getCategory(reader.getNextShortUnsigned());
            this.maxItems = reader.getNextInt();
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextBoolean(this.isItems);
        if (this.isItems) {
            writer.putNextShortUnsigned(this.item.getID());
            this.limits.writePacket(writer);
        } else {
            writer.putNextShortUnsigned(this.category.id);
            writer.putNextInt(this.maxItems);
        }
    }
}

