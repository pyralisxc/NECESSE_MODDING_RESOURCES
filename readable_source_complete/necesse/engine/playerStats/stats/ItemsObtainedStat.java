/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.platforms.Platform;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class ItemsObtainedStat
extends GameStat {
    protected HashSet<String> dirtyItems = new HashSet();
    protected HashSet<String> statItems = new HashSet();
    protected HashSet<String> allItems = new HashSet();

    public ItemsObtainedStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyItems.clear();
    }

    protected void addItem(String itemStringID, boolean updatePlatform, HashSet<String> handled) {
        if (this.allItems.contains(itemStringID) || handled.contains(itemStringID)) {
            return;
        }
        if (ItemRegistry.itemExists(itemStringID)) {
            int id = ItemRegistry.getItemID(itemStringID);
            this.allItems.add(itemStringID);
            if (ItemRegistry.countsInStats(id)) {
                this.statItems.add(itemStringID);
            }
            if (updatePlatform) {
                this.updatePlatform();
            }
            this.markImportantDirty();
            this.dirtyItems.add(itemStringID);
            handled.add(itemStringID);
            for (String nextItemStringID : ItemRegistry.getObtainsOtherItemStringIDs(itemStringID)) {
                this.addItem(nextItemStringID, updatePlatform, handled);
            }
        }
    }

    protected void addItem(String itemStringID, boolean updatePlatform) {
        this.addItem(itemStringID, updatePlatform, new HashSet<String>());
    }

    public void addItem(String itemStringID) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.addItem(itemStringID, true);
    }

    public boolean isItemObtained(String itemStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.allItems.contains(itemStringID);
    }

    public boolean isStatItemObtained(String itemStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.statItems.contains(itemStringID);
    }

    public Iterable<String> getItemsObtained() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.allItems;
    }

    public Iterable<String> getStatItemsObtained() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.statItems;
    }

    public int getTotalItems() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.allItems.size();
    }

    public int getTotalStatItems() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.statItems.size();
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof ItemsObtainedStat) {
            ItemsObtainedStat other = (ItemsObtainedStat)stat;
            for (String item : other.allItems) {
                this.addItem(item, true);
            }
        }
    }

    public void addAllItemsToSet(HashSet<String> set) {
        set.addAll(this.allItems);
    }

    @Override
    public void resetCombine() {
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            return;
        }
        Platform.getStatsProvider().setStat("items_obtained", this.statItems.size());
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
    }

    @Override
    public void addSaveData(SaveData save) {
        if (this.allItems.isEmpty()) {
            return;
        }
        save.addStringHashSet("items", this.allItems);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.allItems.clear();
        this.statItems.clear();
        for (String item : save.getStringHashSet("items", new HashSet<String>())) {
            if (item.isEmpty()) continue;
            this.addItem(item, false);
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.allItems.size());
        for (String item : this.allItems) {
            writer.putNextShortUnsigned(ItemRegistry.getItemID(item));
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.allItems.clear();
        this.statItems.clear();
        this.dirtyItems.clear();
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int itemID = reader.getNextShortUnsigned();
            String itemStringID = ItemRegistry.getItemStringID(itemID);
            this.addItem(itemStringID, true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyItems.size());
        for (String item : this.dirtyItems) {
            writer.putNextShortUnsigned(ItemRegistry.getItemID(item));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int itemID = reader.getNextShortUnsigned();
            String itemStringID = ItemRegistry.getItemStringID(itemID);
            this.addItem(itemStringID, true);
        }
    }
}

