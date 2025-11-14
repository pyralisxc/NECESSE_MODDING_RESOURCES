/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats;

import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class EmptyStats {
    private final ArrayList<GameStat> list = new ArrayList();
    private boolean dirty;
    private boolean importantDirty;
    public final boolean controlAchievements;
    public final Mode mode;

    public EmptyStats(boolean controlAchievements, Mode mode) {
        this.controlAchievements = controlAchievements;
        this.mode = mode;
    }

    protected <T extends GameStat> T addStat(T stat) {
        if (this.list.stream().anyMatch(s -> s.stringID.equals(stat.stringID))) {
            throw new IllegalArgumentException("Cannot add stat with duplicate stringID: " + stat.stringID);
        }
        this.list.add(stat);
        return stat;
    }

    public Stream<GameStat> streamStats() {
        return this.list.stream();
    }

    public void combine(EmptyStats stats) {
        for (int i = 0; i < this.list.size() && i < stats.list.size(); ++i) {
            this.list.get(i).combine(stats.list.get(i));
        }
    }

    public void combineDirty(EmptyStats stats) {
        for (int i = 0; i < this.list.size() && i < stats.list.size(); ++i) {
            if (!stats.list.get(i).isDirty()) continue;
            this.list.get(i).combine(stats.list.get(i));
        }
    }

    public void resetCombine() {
        this.list.forEach(GameStat::resetCombine);
    }

    public void loadStatsFromPlatform(GameStats stats) {
        this.list.forEach(s -> s.loadStatFromPlatform(stats));
    }

    public GameStat getStat(String stringID) {
        return this.list.stream().filter(s -> s.stringID.equals(stringID)).findFirst().orElse(null);
    }

    public void addSaveData(SaveData save) {
        for (GameStat stat : this.list) {
            SaveData statSave = new SaveData(stat.stringID);
            stat.addSaveData(statSave);
            if (statSave.isEmpty()) continue;
            save.addSaveData(statSave);
        }
    }

    public void applyLoadData(LoadData save) {
        for (LoadData statSave : save.getLoadData()) {
            GameStat stat;
            String name = statSave.getName();
            if (name.equals("levels_visited")) {
                name = "discovered_journal_entries";
            }
            if ((stat = this.getStat(name)) == null) continue;
            stat.applyLoadData(statSave);
        }
    }

    public void setupContentPacket(PacketWriter writer) {
        for (GameStat stat : this.list) {
            stat.setupContentPacket(writer);
        }
    }

    public void applyContentPacket(PacketReader reader) {
        for (GameStat stat : this.list) {
            stat.applyContentPacket(reader);
        }
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty || this.importantDirty;
    }

    public void cleanAll() {
        this.list.forEach(GameStat::clean);
        this.dirty = false;
        this.importantDirty = false;
    }

    public void markImportantDirty() {
        this.importantDirty = true;
    }

    public boolean isImportantDirty() {
        return this.importantDirty;
    }

    public void setupDirtyPacket(PacketWriter writer) {
        for (int i = 0; i < this.list.size(); ++i) {
            GameStat stat = this.list.get(i);
            if (!stat.isDirty()) continue;
            writer.putNextShortUnsigned(i);
            stat.setupDirtyPacket(writer);
        }
        writer.putNextShortUnsigned(Short.MAX_VALUE);
    }

    public void applyDirtyPacket(PacketReader reader) {
        int index;
        while ((index = reader.getNextShortUnsigned()) != Short.MAX_VALUE) {
            GameStat stat = this.list.get(index);
            stat.applyDirtyPacket(reader);
        }
        return;
    }

    public static enum Mode {
        READ_ONLY,
        WRITE_ONLY,
        READ_AND_WRITE;

    }
}

