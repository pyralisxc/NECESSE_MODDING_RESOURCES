/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.itemFilter;

import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public class ItemCategoriesFilterChange {
    private Packet packet;

    private ItemCategoriesFilterChange(Packet packet) {
        this.packet = packet;
    }

    public boolean applyTo(ItemCategoriesFilter filter) {
        if (filter == null) {
            return false;
        }
        PacketReader reader = new PacketReader(this.packet);
        ChangeType[] types = ChangeType.values();
        int typeIndex = reader.getNextMaxValue(types.length + 1);
        if (typeIndex < 0 || typeIndex >= types.length) {
            GameLog.warn.println("Tried to apply invalid ItemCategoriesFilterChange to type index " + typeIndex);
            return false;
        }
        ChangeType type = types[typeIndex];
        switch (type) {
            case ITEMS_ALLOWED: {
                int itemsCount = reader.getNextShortUnsigned();
                Item[] items = new Item[itemsCount];
                for (int i = 0; i < items.length; ++i) {
                    items[i] = ItemRegistry.getItem(reader.getNextShortUnsigned());
                    if (items[i] != null) continue;
                    return false;
                }
                boolean allowed = reader.getNextBoolean();
                boolean out = false;
                for (Item item : items) {
                    out = filter.setItemAllowed(item, allowed) || out;
                }
                return out;
            }
            case ITEM_LIMITS: {
                int itemID = reader.getNextShortUnsigned();
                Item item = ItemRegistry.getItem(itemID);
                if (item == null) {
                    return false;
                }
                ItemCategoriesFilter.ItemLimits limits = new ItemCategoriesFilter.ItemLimits();
                limits.readPacket(reader);
                return filter.setItemAllowed(item, limits);
            }
            case CATEGORY_ALLOWED: {
                int categoryID = reader.getNextShortUnsigned();
                boolean allowed = reader.getNextBoolean();
                ItemCategoriesFilter.ItemCategoryFilter category = filter.getItemCategory(categoryID);
                if (category == null) break;
                if (allowed && !category.isAllAllowed()) {
                    category.setAllowed(true);
                    return true;
                }
                if (allowed || !category.isAnyAllowed()) break;
                category.setAllowed(false);
                return true;
            }
            case CATEGORY_LIMIT: {
                int categoryID = reader.getNextShortUnsigned();
                int maxAmount = reader.getNextInt();
                ItemCategoriesFilter.ItemCategoryFilter category = filter.getItemCategory(categoryID);
                if (category != null && category.getMaxItems() != maxAmount) {
                    category.setMaxItems(maxAmount);
                    return true;
                }
                return false;
            }
            case FULL: {
                filter.readPacket(reader);
                return true;
            }
        }
        return true;
    }

    public void write(PacketWriter writer) {
        writer.putNextContentPacket(this.packet);
    }

    public static ItemCategoriesFilterChange fromPacket(PacketReader reader) {
        return new ItemCategoriesFilterChange(reader.getNextContentPacket());
    }

    public static ItemCategoriesFilterChange itemsAllowed(Item[] items, boolean allowed) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.ITEMS_ALLOWED.ordinal(), ChangeType.values().length + 1);
        writer.putNextShortUnsigned(items.length);
        for (Item item : items) {
            writer.putNextShortUnsigned(item.getID());
        }
        writer.putNextBoolean(allowed);
        return new ItemCategoriesFilterChange(packet);
    }

    public static ItemCategoriesFilterChange itemLimits(Item item, ItemCategoriesFilter.ItemLimits limits) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.ITEM_LIMITS.ordinal(), ChangeType.values().length + 1);
        writer.putNextShortUnsigned(item.getID());
        limits.writePacket(writer);
        return new ItemCategoriesFilterChange(packet);
    }

    public static ItemCategoriesFilterChange categoryAllowed(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.CATEGORY_ALLOWED.ordinal(), ChangeType.values().length + 1);
        writer.putNextShortUnsigned(category.category.id);
        writer.putNextBoolean(allowed);
        return new ItemCategoriesFilterChange(packet);
    }

    public static ItemCategoriesFilterChange categoryLimit(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.CATEGORY_LIMIT.ordinal(), ChangeType.values().length + 1);
        writer.putNextShortUnsigned(category.category.id);
        writer.putNextInt(maxItems);
        return new ItemCategoriesFilterChange(packet);
    }

    public static ItemCategoriesFilterChange fullChange(ItemCategoriesFilter filter) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextMaxValue(ChangeType.FULL.ordinal(), ChangeType.values().length + 1);
        filter.writePacket(writer);
        return new ItemCategoriesFilterChange(packet);
    }

    private static enum ChangeType {
        ITEMS_ALLOWED,
        ITEM_LIMITS,
        CATEGORY_ALLOWED,
        CATEGORY_LIMIT,
        FULL;

    }
}

