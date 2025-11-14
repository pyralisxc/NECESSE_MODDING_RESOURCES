/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldData.incursions;

import java.util.HashMap;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.worldData.incursions.IncursionDataStats;
import necesse.engine.world.worldData.incursions.IncursionTotalTierStats;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;

public class IncursionBiomeStats {
    private int total = 0;
    private final HashMap<Integer, IncursionDataStats> biomeStats = new HashMap();
    private final IncursionTotalTierStats totalTiers = new IncursionTotalTierStats(null);

    public void addSaveData(SaveData save) {
        for (Map.Entry<Integer, IncursionDataStats> entry : this.biomeStats.entrySet()) {
            if (entry.getValue().getTotal() <= 0) continue;
            SaveData biomeSave = new SaveData("BIOME");
            String stringID = IncursionBiomeRegistry.getBiomeStringID(entry.getKey());
            biomeSave.addUnsafeString("biomeStringID", stringID);
            SaveData statsSave = new SaveData("DATA");
            entry.getValue().addSaveData(statsSave);
            biomeSave.addSaveData(statsSave);
            save.addSaveData(biomeSave);
        }
    }

    public void applyLoadData(LoadData save) {
        this.total = 0;
        this.biomeStats.clear();
        this.totalTiers.clear();
        for (LoadData biomeSave : save.getLoadDataByName("BIOME")) {
            try {
                String stringID = biomeSave.getUnsafeString("biomeStringID", null, false);
                if (stringID == null) continue;
                int biomeID = IncursionBiomeRegistry.getBiomeID(stringID);
                if (biomeID != -1) {
                    LoadData statsSave = biomeSave.getFirstLoadDataByName("DATA");
                    if (statsSave == null) continue;
                    IncursionDataStats stats = new IncursionDataStats(new IncursionTotalTierStats(this.totalTiers), true);
                    stats.applyLoadData(statsSave);
                    if (stats.getTotal() <= 0) continue;
                    this.biomeStats.put(biomeID, stats);
                    this.total += stats.getTotal();
                    continue;
                }
                System.err.println("Error loading incursion biome stats: Could not find biome with stringID " + stringID);
            }
            catch (Exception e) {
                System.err.println("Unknown error loading incursion biome stats");
                e.printStackTrace();
            }
        }
    }

    public void setupContentPacket(PacketWriter writer) {
        writer.putNextInt(this.total);
        writer.putNextShortUnsigned(this.biomeStats.size());
        for (Map.Entry<Integer, IncursionDataStats> entry : this.biomeStats.entrySet()) {
            writer.putNextShortUnsigned(entry.getKey());
            entry.getValue().setupContentPacket(writer);
        }
    }

    public void applyContentPacket(PacketReader reader) {
        this.biomeStats.clear();
        this.totalTiers.clear();
        this.total = reader.getNextInt();
        int biomes = reader.getNextShortUnsigned();
        for (int i = 0; i < biomes; ++i) {
            int biomeID = reader.getNextShortUnsigned();
            this.getData(biomeID).applyContentPacket(reader);
        }
    }

    public void combine(IncursionBiomeStats other) {
        this.totalTiers.combine(other.totalTiers);
        this.total += other.total;
        other.biomeStats.forEach((biomeID, data) -> this.getData((int)biomeID).combine((IncursionDataStats)data));
    }

    public void loadPlatformTotal(int total) {
        this.total = Math.max(total, this.total);
    }

    public void reset() {
        this.total = 0;
        this.biomeStats.clear();
        this.totalTiers.clear();
    }

    public int getTotal() {
        return this.total;
    }

    public int getTotal(IncursionBiome biome) {
        return this.getTotal(biome.getID());
    }

    public int getTotal(String biomeStringID) {
        return this.getTotal(IncursionBiomeRegistry.getBiomeID(biomeStringID));
    }

    public int getTotal(int biomeID) {
        IncursionDataStats stats = this.biomeStats.get(biomeID);
        return stats == null ? 0 : stats.getTotal();
    }

    public IncursionDataStats getData(IncursionBiome biome) {
        return this.getData(biome.getID());
    }

    public IncursionDataStats getData(String biomeStringID) {
        return this.getData(IncursionBiomeRegistry.getBiomeID(biomeStringID));
    }

    public IncursionDataStats getData(int biomeID) {
        return this.biomeStats.compute(biomeID, (key, last) -> last == null ? new IncursionDataStats(new IncursionTotalTierStats(this.totalTiers), true) : last);
    }

    public int getTotalTiersBelow(int tier, boolean inclusive) {
        return this.totalTiers.getTotalBelow(false, tier, inclusive);
    }

    public int getTotalTiersAbove(int tier, boolean inclusive) {
        return this.totalTiers.getTotalAbove(false, tier, inclusive);
    }

    public int getTotalTiers(int tier) {
        return this.totalTiers.get(false, tier);
    }

    public void add(BiomeMissionIncursionData incursion) {
        int biomeID = incursion.biome.getID();
        this.getData(biomeID).add(incursion);
        ++this.total;
    }
}

