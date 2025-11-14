/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.util.HashSet;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.incursion.AltarStats;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionData;

public class AltarData {
    public HashSet<Integer> obtainedPerkIDs = new HashSet();
    public AltarStats altarStats = new AltarStats(EmptyStats.Mode.READ_AND_WRITE);

    public void addSaveData(SaveData save) {
        HashSet stringIDs = this.obtainedPerkIDs.stream().map(IncursionPerksRegistry::getPerkStringID).collect(Collectors.toCollection(HashSet::new));
        save.addStringHashSet("obtainedPerks", stringIDs);
        SaveData statsSave = new SaveData("stats");
        this.altarStats.addSaveData(statsSave);
        save.addSaveData(statsSave);
    }

    public void applyLoadData(LoadData save) {
        LoadData statsSave;
        HashSet<String> stringIDs = save.getStringHashSet("obtainedPerks", null);
        if (stringIDs != null) {
            for (String stringID : stringIDs) {
                int perkID = IncursionPerksRegistry.getPerkID(stringID);
                if (perkID != -1) {
                    this.obtainedPerkIDs.add(perkID);
                    continue;
                }
                GameLog.warn.println("Could not load incursion perk with string id: " + stringID);
            }
        }
        if ((statsSave = save.getFirstLoadDataByName("stats")) != null) {
            this.altarStats.applyLoadData(statsSave);
        }
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextInt(this.obtainedPerkIDs.size());
        for (Integer obtainedPerkID : this.obtainedPerkIDs) {
            writer.putNextInt(obtainedPerkID);
        }
        this.altarStats.setupContentPacket(writer);
    }

    public void applyPacket(PacketReader reader) {
        this.obtainedPerkIDs.clear();
        int size = reader.getNextInt();
        for (int i = 0; i < size; ++i) {
            this.obtainedPerkIDs.add(reader.getNextInt());
        }
        this.altarStats.applyContentPacket(reader);
    }

    public void init() {
    }

    public String getDebugString() {
        return "to-do add string";
    }

    public void obtainPerk(IncursionPerk perk) {
        this.obtainedPerkIDs.add(perk.getID());
    }

    public void removePerk(IncursionPerk perk) {
        this.obtainedPerkIDs.remove(perk.getID());
    }

    public boolean hasPerk(IncursionPerk perk) {
        return this.obtainedPerkIDs.contains(perk.getID());
    }

    public int getAltarTier() {
        int tier = IncursionData.MINIMUM_TIER - 1;
        for (Integer obtainedPerkID : this.obtainedPerkIDs) {
            int currentPerkTier = IncursionPerksRegistry.getPerk((int)obtainedPerkID.intValue()).tier;
            if (currentPerkTier <= tier) continue;
            tier = currentPerkTier;
        }
        return Math.min(tier, IncursionData.TABLET_TIER_UPGRADE_CAP);
    }

    public int getAltarTierAfterPerkRespec(IncursionPerk perkBeingRespecced) {
        int tier = IncursionData.MINIMUM_TIER - 1;
        for (Integer obtainedPerkID : this.obtainedPerkIDs) {
            int currentPerkTier;
            if (IncursionPerksRegistry.getPerk(obtainedPerkID) == perkBeingRespecced || (currentPerkTier = IncursionPerksRegistry.getPerk((int)obtainedPerkID.intValue()).tier) <= tier) continue;
            tier = currentPerkTier;
        }
        return Math.min(tier, IncursionData.TABLET_TIER_UPGRADE_CAP);
    }

    public boolean areTherePerksLeftAfterRespecOnThisTier(IncursionPerk perkBeingRespecced, int tier) {
        for (Integer obtainedPerkID : this.obtainedPerkIDs) {
            IncursionPerk perk = IncursionPerksRegistry.getPerk(obtainedPerkID);
            if (perkBeingRespecced == perk || !IncursionPerksRegistry.getPerksAtTier(tier).contains(perk)) continue;
            return true;
        }
        return false;
    }

    public void addCompletedIncursion(IncursionData data) {
        if (data instanceof BiomeMissionIncursionData) {
            this.altarStats.completed_incursions.add((BiomeMissionIncursionData)data);
        }
    }

    public int getCompletedIncursionsAtSpecificTierOrHigher(int tier) {
        return this.altarStats.completed_incursions.getTotalTiersAbove(tier, true);
    }

    public boolean checkForSpecificIncursionCompletedAtSpecificTierOrAbove(int incursionBiomeID, int tier) {
        return this.altarStats.completed_incursions.getData(incursionBiomeID).getTotalTiersAbove(tier, true) > 0;
    }

    public boolean checkForAmountOfIncursionsCompletedAtSpecificTierOrAbove(int tier, int amount) {
        return this.altarStats.completed_incursions.getTotalTiersAbove(tier, true) >= amount;
    }

    public boolean isSameAltarData(AltarData other) {
        return other.obtainedPerkIDs == this.obtainedPerkIDs;
    }

    public AltarData makeCopy() {
        AltarData altarData = new AltarData();
        altarData.obtainedPerkIDs = this.obtainedPerkIDs;
        return altarData;
    }
}

