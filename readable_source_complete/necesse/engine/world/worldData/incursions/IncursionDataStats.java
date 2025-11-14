/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldData.incursions;

import java.util.HashMap;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IncursionDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.worldData.incursions.IncursionTierStats;
import necesse.engine.world.worldData.incursions.IncursionTotalTierStats;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionData;

public class IncursionDataStats {
    private int total = 0;
    private final HashMap<Integer, IncursionTierStats> dataStats = new HashMap();
    private final IncursionTotalTierStats totalTiers;
    private final boolean clearTotalTiers;

    public IncursionDataStats(IncursionTotalTierStats totalTiers, boolean clearTotalTiers) {
        this.totalTiers = totalTiers;
        this.clearTotalTiers = clearTotalTiers;
    }

    public void addSaveData(SaveData save) {
        for (Map.Entry<Integer, IncursionTierStats> entry : this.dataStats.entrySet()) {
            if (entry.getValue().getTotal() <= 0) continue;
            String stringID = IncursionDataRegistry.getIncursionDataStringID(entry.getKey());
            SaveData tierSave = new SaveData(stringID);
            entry.getValue().addSaveData(tierSave);
            save.addSaveData(tierSave);
        }
    }

    public void applyLoadData(LoadData save) {
        this.total = 0;
        this.dataStats.clear();
        if (this.clearTotalTiers) {
            this.totalTiers.clear();
        }
        for (LoadData data : save.getLoadData()) {
            try {
                String dataStringID = data.getName().trim();
                int dataID = IncursionDataRegistry.getIncursionDataID(dataStringID);
                if (dataID != -1) {
                    int count;
                    if (data.isArray()) {
                        IncursionTierStats stats = new IncursionTierStats(this.totalTiers);
                        stats.applyLoadData(data);
                        if (stats.getTotal() <= 0) continue;
                        this.dataStats.put(dataID, stats);
                        this.total += stats.getTotal();
                        continue;
                    }
                    try {
                        count = Integer.parseInt(data.getData().trim());
                    }
                    catch (NumberFormatException e) {
                        count = 0;
                    }
                    IncursionTierStats tierStats = new IncursionTierStats(this.totalTiers);
                    this.dataStats.put(dataID, tierStats);
                    tierStats.add(0, count);
                    this.total += count;
                    continue;
                }
                System.err.println("Error loading incursion data stats: Could not find data with stringID " + dataStringID);
            }
            catch (Exception e) {
                System.err.println("Unknown error loading incursion data stats");
                e.printStackTrace();
            }
        }
    }

    public void setupContentPacket(PacketWriter writer) {
        writer.putNextInt(this.total);
        writer.putNextShortUnsigned(this.dataStats.size());
        for (Map.Entry<Integer, IncursionTierStats> entry : this.dataStats.entrySet()) {
            writer.putNextShortUnsigned(entry.getKey());
            entry.getValue().setupContentPacket(writer);
        }
    }

    public void applyContentPacket(PacketReader reader) {
        if (this.clearTotalTiers) {
            this.totalTiers.clear();
        }
        this.total = reader.getNextInt();
        int statsCount = reader.getNextShortUnsigned();
        for (int i = 0; i < statsCount; ++i) {
            int dataID = reader.getNextShortUnsigned();
            this.getTierData(dataID).applyContentPacket(reader);
        }
    }

    public void combine(IncursionDataStats other) {
        this.totalTiers.combine(other.totalTiers);
        this.total += other.total;
        other.dataStats.forEach((biomeID, data) -> this.getTierData((int)biomeID).combine((IncursionTierStats)data));
    }

    public int getTotal() {
        return this.total;
    }

    public IncursionTierStats getTierData(IncursionData incursionData) {
        return this.getTierData(incursionData.getID());
    }

    public IncursionTierStats getTierData(String incursionDataStringID) {
        return this.getTierData(IncursionDataRegistry.getIncursionDataID(incursionDataStringID));
    }

    public IncursionTierStats getTierData(int incursionDataID) {
        return this.dataStats.compute(incursionDataID, (key, last) -> last == null ? new IncursionTierStats(this.totalTiers) : last);
    }

    public int getTotal(IncursionData data) {
        return this.getTotal(data.getID());
    }

    public int getTotal(String dataStringID) {
        return this.getTotal(IncursionDataRegistry.getIncursionDataID(dataStringID));
    }

    public int getTotal(Class<? extends IncursionData> dataClass) {
        return this.getTotal(IncursionDataRegistry.getIncursionDataID(dataClass));
    }

    public int getTotal(int incursionDataID) {
        return this.getTierData(incursionDataID).getTotal();
    }

    public void add(BiomeMissionIncursionData incursion) {
        int dataID = incursion.getID();
        this.getTierData(dataID).add(incursion);
        ++this.total;
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
}

