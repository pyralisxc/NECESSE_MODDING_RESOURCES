/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.item.Item;

public class TrinketsWornStat
extends GameStat {
    protected HashSet<String> dirtyTrinkets = new HashSet();
    protected HashSet<String> trinkets = new HashSet();

    public TrinketsWornStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyTrinkets.clear();
    }

    protected void addTrinketWorn(String trinketStringID, boolean updateSteam) {
        Item item;
        if (this.trinkets.contains(trinketStringID)) {
            return;
        }
        if (ItemRegistry.itemExists(trinketStringID) && (item = ItemRegistry.getItem(trinketStringID)).isTrinketItem()) {
            this.trinkets.add(trinketStringID);
            if (updateSteam) {
                this.updatePlatform();
            }
            this.dirtyTrinkets.add(trinketStringID);
            this.markImportantDirty();
        }
    }

    public void addTrinketWorn(Item item) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        if (!item.isTrinketItem()) {
            return;
        }
        this.addTrinketWorn(item.getStringID(), true);
    }

    public boolean isTrinketWorn(String itemStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.trinkets.contains(itemStringID);
    }

    public Iterable<String> getTrinketsWorn() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.trinkets;
    }

    public int getTotalTrinketsWorn() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.trinkets.size();
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof TrinketsWornStat) {
            TrinketsWornStat other = (TrinketsWornStat)stat;
            for (String trinket : other.trinkets) {
                this.addTrinketWorn(trinket, true);
            }
        }
    }

    @Override
    public void resetCombine() {
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            // empty if block
        }
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
    }

    @Override
    public void addSaveData(SaveData save) {
        if (this.trinkets.isEmpty()) {
            return;
        }
        save.addStringHashSet("trinkets", this.trinkets);
    }

    @Override
    public void applyLoadData(LoadData save) {
        for (String trinket : save.getStringHashSet("trinkets", new HashSet<String>())) {
            if (trinket.isEmpty()) continue;
            this.addTrinketWorn(trinket, false);
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.trinkets.size());
        for (String trinket : this.trinkets) {
            writer.putNextShortUnsigned(ItemRegistry.getItemID(trinket));
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.trinkets.clear();
        this.dirtyTrinkets.clear();
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int trinketID = reader.getNextShortUnsigned();
            String trinketStringID = ItemRegistry.getItemStringID(trinketID);
            this.addTrinketWorn(trinketStringID, true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyTrinkets.size());
        for (String trinket : this.dirtyTrinkets) {
            writer.putNextShortUnsigned(ItemRegistry.getItemID(trinket));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int trinketID = reader.getNextShortUnsigned();
            String trinketStringID = ItemRegistry.getItemStringID(trinketID);
            this.addTrinketWorn(trinketStringID, true);
        }
    }
}

