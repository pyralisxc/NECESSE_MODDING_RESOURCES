/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.platforms.Platform;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class IncIntGameStat
extends GameStat {
    protected final int defaultValue;
    protected int value;
    protected boolean isImportant;
    protected boolean isPlatformStat;

    public IncIntGameStat(EmptyStats parent, String stringID, int defaultValue, boolean isImportant, boolean isPlatformStat) {
        super(parent, stringID);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.isImportant = isImportant;
        this.isPlatformStat = isPlatformStat;
    }

    public IncIntGameStat(EmptyStats parent, String stringID, boolean isImportant, boolean isPlatformStat) {
        this(parent, stringID, 0, isImportant, isPlatformStat);
    }

    public int get() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.value;
    }

    public void increment(int increment) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        if (increment == 0) {
            return;
        }
        this.value += increment;
        if (this.isImportant) {
            this.markImportantDirty();
        } else {
            this.markDirty();
        }
        this.updatePlatform();
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof IncIntGameStat) {
            IncIntGameStat other = (IncIntGameStat)stat;
            if (other.value != 0) {
                this.value = other.value + this.value;
                if (this.isImportant) {
                    this.markImportantDirty();
                } else {
                    this.markDirty();
                }
                this.updatePlatform();
            }
        }
    }

    @Override
    public void resetCombine() {
        this.value = this.defaultValue;
    }

    protected void updatePlatform() {
        if (!this.isPlatformStat) {
            return;
        }
        if (!this.parent.controlAchievements) {
            return;
        }
        Platform.getStatsProvider().setStat(this.stringID, this.value);
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
        if (!this.isPlatformStat) {
            return;
        }
        this.value = stats.getStatByName(this.stringID, this.value);
    }

    @Override
    public void addSaveData(SaveData save) {
        if (this.value == this.defaultValue) {
            return;
        }
        save.addInt("value", this.value);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.value = save.getInt("value", this.value);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextInt(this.value);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.value = reader.getNextInt();
        this.updatePlatform();
    }
}

