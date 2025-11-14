/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.HashSet;
import java.util.NoSuchElementException;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;

public class SetBonusesWornStat
extends GameStat {
    protected HashSet<String> dirtySets = new HashSet();
    protected HashSet<String> sets = new HashSet();

    public SetBonusesWornStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtySets.clear();
    }

    protected void addSetBonusWorn(String setBonusStringID, boolean updateSteam) {
        if (this.sets.contains(setBonusStringID)) {
            return;
        }
        try {
            int id = BuffRegistry.getBuffIDRaw(setBonusStringID);
            Buff buff = BuffRegistry.getBuff(id);
            if (buff instanceof SetBonusBuff) {
                this.sets.add(setBonusStringID);
                if (updateSteam) {
                    this.updatePlatform();
                }
                this.dirtySets.add(setBonusStringID);
                this.markImportantDirty();
            }
        }
        catch (NoSuchElementException noSuchElementException) {
            // empty catch block
        }
    }

    public void addSetBonusWorn(SetBonusBuff setBonusBuff) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.addSetBonusWorn(setBonusBuff.getStringID(), true);
    }

    public boolean isSetBonusWorn(String setBonusStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.sets.contains(setBonusStringID);
    }

    public Iterable<String> getSetBonusesWorn() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.sets;
    }

    public int getTotalSetBonusesWorn() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.sets.size();
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof SetBonusesWornStat) {
            SetBonusesWornStat other = (SetBonusesWornStat)stat;
            for (String set : other.sets) {
                this.addSetBonusWorn(set, true);
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
        if (this.sets.isEmpty()) {
            return;
        }
        save.addStringHashSet("sets", this.sets);
    }

    @Override
    public void applyLoadData(LoadData save) {
        for (String set : save.getStringHashSet("sets", new HashSet<String>())) {
            if (set.isEmpty()) continue;
            this.addSetBonusWorn(set, false);
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.sets.size());
        for (String buff : this.sets) {
            writer.putNextShortUnsigned(BuffRegistry.getBuffID(buff));
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.sets.clear();
        this.dirtySets.clear();
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int buffID = reader.getNextShortUnsigned();
            String buffStringID = BuffRegistry.getBuffStringID(buffID);
            this.addSetBonusWorn(buffStringID, true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtySets.size());
        for (String buff : this.dirtySets) {
            writer.putNextShortUnsigned(BuffRegistry.getBuffID(buff));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int buffID = reader.getNextShortUnsigned();
            String buffStringID = BuffRegistry.getBuffStringID(buffID);
            this.addSetBonusWorn(buffStringID, true);
        }
    }
}

