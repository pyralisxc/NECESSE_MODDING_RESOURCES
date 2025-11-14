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
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;

public class RaidEventsStat
extends GameStat {
    protected HashSet<String> dirtyRaidTypes = new HashSet();
    protected HashMap<String, Integer> raidTypes = new HashMap();
    protected int totalRaids = 0;

    public RaidEventsStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyRaidTypes.clear();
    }

    protected void setCount(String raidType, int amount, boolean updateSteam) {
        int eventID = LevelEventRegistry.getEventID(raidType);
        if (eventID != -1) {
            int prevStat = this.raidTypes.getOrDefault(raidType, 0);
            if (prevStat == amount) {
                return;
            }
            this.raidTypes.put(raidType, amount);
            int delta = amount - prevStat;
            this.totalRaids += delta;
            if (updateSteam) {
                this.updatePlatform();
            }
            this.dirtyRaidTypes.add(raidType);
            this.markImportantDirty();
        }
    }

    public void addRaid(String raidType) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.setCount(raidType, this.raidTypes.getOrDefault(raidType, 0) + 1, true);
    }

    public void addRaid(SettlementRaidLevelEvent event) {
        this.addRaid(event.getStringID());
    }

    public int getRaidCount(String raidType) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.raidTypes.getOrDefault(raidType, 0);
    }

    public void forEach(BiConsumer<String, Integer> action) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        this.raidTypes.forEach(action);
    }

    public int getTotalRaids() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.totalRaids;
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof RaidEventsStat) {
            RaidEventsStat other = (RaidEventsStat)stat;
            other.raidTypes.forEach((? super K s, ? super V v) -> this.setCount((String)s, this.raidTypes.getOrDefault(s, 0) + v, true));
        }
    }

    @Override
    public void resetCombine() {
        this.raidTypes.clear();
        this.totalRaids = 0;
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            return;
        }
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
    }

    @Override
    public void addSaveData(SaveData save) {
        this.raidTypes.forEach(save::addInt);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.raidTypes.clear();
        this.totalRaids = 0;
        for (LoadData data : save.getLoadData()) {
            if (!data.isData()) continue;
            try {
                int amount = LoadData.getInt(data);
                this.setCount(data.getName(), amount, false);
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not load event stat number: " + data.getData());
            }
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.raidTypes.size());
        this.raidTypes.forEach((? super K id, ? super V amount) -> {
            writer.putNextShortUnsigned(LevelEventRegistry.getEventID(id));
            writer.putNextInt((int)amount);
        });
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.raidTypes.clear();
        this.dirtyRaidTypes.clear();
        this.totalRaids = 0;
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int eventID = reader.getNextShortUnsigned();
            String eventStringID = LevelEventRegistry.getEventStringID(eventID);
            this.setCount(eventStringID, reader.getNextInt(), true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyRaidTypes.size());
        for (String eventStringID : this.dirtyRaidTypes) {
            writer.putNextShortUnsigned(LevelEventRegistry.getEventID(eventStringID));
            writer.putNextInt(this.raidTypes.getOrDefault(eventStringID, 0));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int eventID = reader.getNextShortUnsigned();
            String eventStringID = LevelEventRegistry.getEventStringID(eventID);
            this.setCount(eventStringID, reader.getNextInt(), true);
        }
    }
}

