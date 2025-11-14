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
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementStorageChangeAllowedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int tileX;
    public final int tileY;
    public final boolean isItems;
    public final boolean allowed;
    public final Item[] items;
    public final ItemCategory category;

    public SettlementStorageChangeAllowedEvent(ServerSettlementData data, int tileX, int tileY, Item[] items, boolean allowed) {
        this.settlementUniqueID = data.uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.allowed = allowed;
        this.isItems = true;
        this.items = items;
        this.category = null;
    }

    public SettlementStorageChangeAllowedEvent(ServerSettlementData data, int tileX, int tileY, ItemCategory category, boolean allowed) {
        this.settlementUniqueID = data.uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.allowed = allowed;
        this.isItems = false;
        this.items = null;
        this.category = category;
    }

    public SettlementStorageChangeAllowedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.allowed = reader.getNextBoolean();
        this.isItems = reader.getNextBoolean();
        if (this.isItems) {
            this.category = null;
            int itemsLength = reader.getNextShortUnsigned();
            this.items = new Item[itemsLength];
            for (int i = 0; i < itemsLength; ++i) {
                int itemID = reader.getNextShortUnsigned();
                this.items[i] = ItemRegistry.getItem(itemID);
            }
        } else {
            this.items = null;
            this.category = ItemCategory.getCategory(reader.getNextShortUnsigned());
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextBoolean(this.allowed);
        writer.putNextBoolean(this.isItems);
        if (this.isItems) {
            writer.putNextShortUnsigned(this.items.length);
            for (Item item : this.items) {
                writer.putNextShortUnsigned(item.getID());
            }
        } else {
            writer.putNextShortUnsigned(this.category.id);
        }
    }
}

