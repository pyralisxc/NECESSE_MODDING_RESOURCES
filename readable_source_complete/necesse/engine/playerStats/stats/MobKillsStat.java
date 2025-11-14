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
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;

public class MobKillsStat
extends GameStat {
    protected HashSet<String> dirtyKills = new HashSet();
    protected HashMap<String, Integer> kills = new HashMap();
    protected int totalKills = 0;
    protected int bossKills = 0;

    public MobKillsStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyKills.clear();
    }

    protected void setKills(String mobStringID, int amount, boolean updateSteam) {
        int mobID = MobRegistry.getMobID(mobStringID);
        if (mobID != -1 && MobRegistry.countMobKillStat(mobID)) {
            int prevStat = this.kills.getOrDefault(mobStringID, 0);
            if (prevStat == amount) {
                return;
            }
            this.kills.put(mobStringID, amount);
            int delta = amount - prevStat;
            this.totalKills += delta;
            if (MobRegistry.isBossMob(mobID)) {
                this.bossKills += delta;
            }
            if (updateSteam) {
                this.updatePlatform();
            }
            this.dirtyKills.add(mobStringID);
            this.markImportantDirty();
        }
    }

    public void addKill(String mobStringID) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.setKills(mobStringID, this.kills.getOrDefault(mobStringID, 0) + 1, true);
    }

    public void addKill(Mob mob) {
        this.addKill(mob.getStringID());
    }

    public int getKills(String mobStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.kills.getOrDefault(mobStringID, 0);
    }

    public void forEach(BiConsumer<String, Integer> action) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        this.kills.forEach(action);
    }

    public int getTotalKills() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.totalKills;
    }

    public int getBossKills() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.bossKills;
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof MobKillsStat) {
            MobKillsStat other = (MobKillsStat)stat;
            other.kills.forEach((? super K s, ? super V v) -> this.setKills((String)s, this.kills.getOrDefault(s, 0) + v, true));
        }
    }

    @Override
    public void resetCombine() {
        this.kills.clear();
        this.totalKills = 0;
        this.bossKills = 0;
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            return;
        }
        Platform.getStatsProvider().setStat("mob_kills", this.totalKills);
        Platform.getStatsProvider().setStat("boss_kills", this.bossKills);
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
        this.totalKills = stats.mob_kills;
        this.bossKills = stats.boss_kills;
    }

    @Override
    public void addSaveData(SaveData save) {
        this.kills.forEach(save::addInt);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.kills.clear();
        this.totalKills = 0;
        this.bossKills = 0;
        for (LoadData data : save.getLoadData()) {
            if (!data.isData()) continue;
            try {
                int amount = LoadData.getInt(data);
                this.setKills(data.getName(), amount, false);
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not load mob kills stat number: " + data.getData());
            }
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.kills.size());
        this.kills.forEach((? super K id, ? super V amount) -> {
            writer.putNextShortUnsigned(MobRegistry.getMobID(id));
            writer.putNextInt((int)amount);
        });
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.kills.clear();
        this.dirtyKills.clear();
        this.totalKills = 0;
        this.bossKills = 0;
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int mobID = reader.getNextShortUnsigned();
            String mobStringID = MobRegistry.getMobStringID(mobID);
            this.setKills(mobStringID, reader.getNextInt(), true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyKills.size());
        for (String mobStringID : this.dirtyKills) {
            writer.putNextShortUnsigned(MobRegistry.getMobID(mobStringID));
            writer.putNextInt(this.kills.getOrDefault(mobStringID, 0));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int mobID = reader.getNextShortUnsigned();
            String mobStringID = MobRegistry.getMobStringID(mobID);
            this.setKills(mobStringID, reader.getNextInt(), true);
        }
    }
}

