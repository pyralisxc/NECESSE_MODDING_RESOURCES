/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStats;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public abstract class GameStat {
    public final EmptyStats parent;
    public final String stringID;
    private boolean dirty;
    private boolean importantDirty;

    public GameStat(EmptyStats parent, String stringID) {
        this.parent = parent;
        this.stringID = stringID;
    }

    public GameMessage getDisplayName() {
        return new LocalMessage("stats", this.stringID);
    }

    public boolean isDirty() {
        return this.dirty || this.importantDirty;
    }

    public void markDirty() {
        this.dirty = true;
        this.parent.markDirty();
    }

    public void clean() {
        this.dirty = false;
        this.importantDirty = false;
    }

    public boolean isImportantDirty() {
        return this.importantDirty;
    }

    public void markImportantDirty() {
        this.importantDirty = true;
        this.parent.markImportantDirty();
    }

    public abstract void combine(GameStat var1);

    public abstract void resetCombine();

    public abstract void loadStatFromPlatform(GameStats var1);

    public abstract void addSaveData(SaveData var1);

    public abstract void applyLoadData(LoadData var1);

    public abstract void setupContentPacket(PacketWriter var1);

    public abstract void applyContentPacket(PacketReader var1);

    public void setupDirtyPacket(PacketWriter writer) {
        this.setupContentPacket(writer);
    }

    public void applyDirtyPacket(PacketReader reader) {
        this.applyContentPacket(reader);
    }
}

