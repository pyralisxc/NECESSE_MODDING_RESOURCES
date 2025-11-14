/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.itemFilter;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameMath;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.maps.Level;

public class ItemFilterList
implements Iterable<ItemFilter> {
    public final ArrayList<ItemFilter> filters;
    public final int minAmount;
    public final int maxAmount;
    public final boolean allowUnfilteredItems;

    public ItemFilterList(int initialCapacity, int minAmount, int maxAmount, boolean allowUnfilteredItems) {
        this.filters = new ArrayList(initialCapacity);
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.allowUnfilteredItems = allowUnfilteredItems;
    }

    public ItemFilterList(int minAmount, int maxAmount, boolean allowUnfilteredItems) {
        this(0, minAmount, maxAmount, allowUnfilteredItems);
    }

    public ItemFilterList() {
        this(Integer.MAX_VALUE, Integer.MAX_VALUE, true);
    }

    public ItemFilterList(LoadData save) {
        this.filters = new ArrayList();
        this.minAmount = save.getInt("minAmount", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
        this.maxAmount = save.getInt("maxAmount", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
        this.allowUnfilteredItems = save.getBoolean("allowUnfilteredItems", true, false);
        for (LoadData filterSave : save.getLoadDataByName("filter")) {
            try {
                this.add(new ItemFilter(filterSave));
            }
            catch (LoadDataException e) {
                GameLog.warn.println("Could not load item filter: " + e.getMessage());
            }
        }
    }

    public void addSaveData(SaveData save) {
        if (this.minAmount != Integer.MAX_VALUE) {
            save.addInt("minAmount", this.minAmount);
        }
        if (this.maxAmount != Integer.MAX_VALUE) {
            save.addInt("maxAmount", this.maxAmount);
        }
        save.addBoolean("allowUnfilteredItems", this.allowUnfilteredItems);
        for (ItemFilter filter : this) {
            SaveData filterSave = new SaveData("filter");
            filter.addSaveData(filterSave);
            save.addSaveData(filterSave);
        }
    }

    public ItemFilterList(PacketReader reader) {
        this.minAmount = reader.getNextInt();
        this.maxAmount = reader.getNextInt();
        this.allowUnfilteredItems = reader.getNextBoolean();
        int size = reader.getNextShortUnsigned();
        this.filters = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            this.add(new ItemFilter(reader));
        }
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextInt(this.minAmount);
        writer.putNextInt(this.maxAmount);
        writer.putNextBoolean(this.allowUnfilteredItems);
        writer.putNextShortUnsigned(this.size());
        for (ItemFilter filter : this) {
            filter.writePacket(writer);
        }
    }

    @Override
    public Iterator<ItemFilter> iterator() {
        return this.filters.iterator();
    }

    public boolean add(ItemFilter filter) {
        if (this.hasFilter(filter)) {
            return false;
        }
        this.filters.add(filter);
        return true;
    }

    public boolean hasFilter(ItemFilter filter) {
        return this.filters.stream().anyMatch(e -> e.isSameFilter(filter));
    }

    public int size() {
        return this.filters.size();
    }

    public ItemFilter get(int index) {
        return this.filters.get(index);
    }

    public void clear() {
        this.filters.clear();
    }

    public ItemFilterList copy() {
        Packet packet = new Packet();
        this.writePacket(new PacketWriter(packet));
        return new ItemFilterList(new PacketReader(packet));
    }

    public int getAddAmount(Level level, InventoryItem item, InventoryRange range) {
        if (this.filters.isEmpty() && !this.allowUnfilteredItems) {
            return 0;
        }
        ComputedObjectValue<InventoryRange, Integer> inventoryAmount = new ComputedObjectValue<InventoryRange, Integer>(range, () -> {
            int amount = 0;
            for (int slot = range.startSlot; slot <= range.endSlot; ++slot) {
                amount += range.inventory.getAmount(slot);
            }
            return amount;
        });
        if ((Integer)inventoryAmount.get() >= this.minAmount) {
            return 0;
        }
        int maxAdd = item.getAmount();
        if (this.maxAmount != Integer.MAX_VALUE) {
            maxAdd = GameMath.limit(this.maxAmount - (Integer)inventoryAmount.get(), 0, item.getAmount());
        }
        if (this.filters.isEmpty()) {
            return Math.min(item.getAmount(), maxAdd);
        }
        boolean matches = false;
        int amount = 0;
        for (ItemFilter filter : this) {
            if (!filter.matchesItem(item)) continue;
            matches = true;
            amount = Math.max(filter.getAddAmount(level, item, range), amount);
            if (amount < item.getAmount()) continue;
            break;
        }
        if (matches) {
            return GameMath.limit(amount, 0, maxAdd);
        }
        return this.allowUnfilteredItems ? item.getAmount() : 0;
    }

    public int getRemoveAmount(Level level, InventoryItem item, InventoryRange range) {
        if (this.filters.isEmpty() && !this.allowUnfilteredItems) {
            return item.getAmount();
        }
        int minRemove = 0;
        if (this.maxAmount != Integer.MAX_VALUE) {
            int inventoryAmount = 0;
            for (int slot = range.startSlot; slot <= range.endSlot; ++slot) {
                inventoryAmount += range.inventory.getAmount(slot);
            }
            minRemove = Math.max(inventoryAmount - this.maxAmount, 0);
        }
        if (this.filters.isEmpty()) {
            return minRemove;
        }
        boolean matches = false;
        int amount = item.getAmount();
        for (ItemFilter filter : this) {
            if (!filter.matchesItem(item)) continue;
            matches = true;
            amount = Math.min(filter.getRemoveAmount(level, item, range), amount);
            if (amount > 0) continue;
            break;
        }
        if (matches) {
            return GameMath.limit(amount, minRemove, item.getAmount());
        }
        return this.allowUnfilteredItems ? 0 : item.getAmount();
    }

    public boolean matchesItem(InventoryItem item) {
        if (this.filters.isEmpty()) {
            return this.allowUnfilteredItems;
        }
        for (ItemFilter filter : this) {
            if (!filter.matchesItem(item)) continue;
            return true;
        }
        return this.allowUnfilteredItems;
    }
}

