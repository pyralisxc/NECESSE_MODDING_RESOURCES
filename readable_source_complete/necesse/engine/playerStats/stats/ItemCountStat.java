/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.platforms.Platform;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.item.Item;

public class ItemCountStat
extends GameStat {
    protected HashSet<String> dirty = new HashSet();
    protected HashMap<String, Integer> counts = new HashMap();
    protected int total = 0;

    public ItemCountStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirty.clear();
    }

    protected void set(String itemStringID, int amount, boolean updateSteam) {
        int itemID = ItemRegistry.getItemID(itemStringID);
        if (itemID != -1) {
            int prevStat = this.counts.getOrDefault(itemStringID, 0);
            if (prevStat == amount) {
                return;
            }
            this.counts.put(itemStringID, amount);
            int delta = amount - prevStat;
            this.total += delta;
            if (updateSteam) {
                this.updatePlatform();
            }
            this.dirty.add(itemStringID);
            this.markImportantDirty();
        }
    }

    protected void add(String itemStringID) {
        this.set(itemStringID, this.counts.getOrDefault(itemStringID, 0) + 1, true);
    }

    public void add(Item item) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.add(item.getStringID());
    }

    public int get(String itemStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.counts.getOrDefault(itemStringID, 0);
    }

    public void forEach(BiConsumer<String, Integer> action) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        this.counts.forEach(action);
    }

    public int getTotal() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.total;
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof ItemCountStat) {
            ItemCountStat other = (ItemCountStat)stat;
            other.counts.forEach((? super K s, ? super V v) -> this.set((String)s, this.counts.getOrDefault(s, 0) + v, true));
        }
    }

    @Override
    public void resetCombine() {
        this.counts.clear();
        this.total = 0;
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            return;
        }
        Platform.getStatsProvider().setStat(this.stringID, this.total);
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
        this.total = stats.getStatByName(this.stringID, this.total);
    }

    @Override
    public void addSaveData(SaveData save) {
        this.counts.forEach(save::addInt);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.counts.clear();
        this.total = 0;
        for (LoadData data : save.getLoadData()) {
            if (!data.isData()) continue;
            try {
                int amount = LoadData.getInt(data);
                this.set(data.getName(), amount, false);
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not load " + this.stringID + " stat number: " + data.getData());
            }
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.counts.size());
        this.counts.forEach((? super K id, ? super V amount) -> {
            writer.putNextShortUnsigned(ItemRegistry.getItemID(id));
            writer.putNextInt((int)amount);
        });
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.counts.clear();
        this.dirty.clear();
        this.total = 0;
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int itemID = reader.getNextShortUnsigned();
            String itemStringID = ItemRegistry.getItemStringID(itemID);
            this.set(itemStringID, reader.getNextInt(), true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirty.size());
        for (String itemStringID : this.dirty) {
            writer.putNextShortUnsigned(ItemRegistry.getItemID(itemStringID));
            writer.putNextInt(this.counts.getOrDefault(itemStringID, 0));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int itemID = reader.getNextShortUnsigned();
            String itemStringID = ItemRegistry.getItemStringID(itemID);
            this.set(itemStringID, reader.getNextInt(), true);
        }
    }
}

