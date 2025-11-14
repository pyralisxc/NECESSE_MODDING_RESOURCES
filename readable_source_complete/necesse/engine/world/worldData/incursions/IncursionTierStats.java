/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldData.incursions;

import java.util.Map;
import java.util.TreeMap;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.worldData.incursions.IncursionTotalTierStats;
import necesse.level.maps.incursion.IncursionData;

public class IncursionTierStats {
    private int total;
    private final TreeMap<Integer, Integer> tierStats = new TreeMap();
    private final IncursionTotalTierStats totalTiers;

    public IncursionTierStats(IncursionTotalTierStats totalTiers) {
        this.totalTiers = totalTiers;
    }

    public void addSaveData(SaveData save) {
        for (Map.Entry<Integer, Integer> entry : this.tierStats.entrySet()) {
            if (entry.getValue() <= 0) continue;
            save.addInt(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public void applyLoadData(LoadData save) {
        this.total = 0;
        this.tierStats.clear();
        for (LoadData data : save.getLoadData()) {
            try {
                int tier = Integer.parseInt(data.getName().trim());
                if (tier >= 0) {
                    int count;
                    try {
                        count = Integer.parseInt(data.getData().trim());
                    }
                    catch (NumberFormatException e) {
                        count = 0;
                    }
                    this.tierStats.put(tier, count);
                    this.total += count;
                    this.totalTiers.add(tier, count);
                    continue;
                }
                System.err.println("Error loading incursion tier stats: Could not add tier 0 or below: " + tier);
            }
            catch (Exception e) {
                System.err.println("Unknown error loading incursion tier stats");
                e.printStackTrace();
            }
        }
    }

    public void setupContentPacket(PacketWriter writer) {
        writer.putNextInt(this.total);
        writer.putNextShortUnsigned(this.tierStats.size());
        for (Map.Entry<Integer, Integer> entry : this.tierStats.entrySet()) {
            writer.putNextShortUnsigned(entry.getKey());
            writer.putNextInt(entry.getValue());
        }
    }

    public void applyContentPacket(PacketReader reader) {
        this.total = reader.getNextInt();
        int statsCount = reader.getNextShortUnsigned();
        for (int i = 0; i < statsCount; ++i) {
            int tierID = reader.getNextShortUnsigned();
            int value = reader.getNextInt();
            this.tierStats.put(tierID, value);
            this.total += value;
            this.totalTiers.add(tierID, value);
        }
    }

    public void combine(IncursionTierStats other) {
        this.total += other.total;
        other.tierStats.forEach((dataID, count) -> {
            int last = this.tierStats.getOrDefault(dataID, 0) + count;
            this.tierStats.put((Integer)dataID, last);
            this.totalTiers.add((int)dataID, last);
        });
    }

    public int getTotal() {
        return this.total;
    }

    public int getTotalBelow(int tier, boolean inclusive) {
        return this.tierStats.headMap(tier, inclusive).values().stream().mapToInt(v -> v).sum();
    }

    public int getTotalAbove(int tier, boolean inclusive) {
        return this.tierStats.tailMap(tier, inclusive).values().stream().mapToInt(v -> v).sum();
    }

    public int getCount(int tier) {
        return this.tierStats.getOrDefault(tier, 0);
    }

    public void add(IncursionData incursion) {
        this.add(incursion.getTabletTier(), 1);
    }

    protected void add(int tier, int count) {
        this.tierStats.compute(tier, (key, last) -> {
            if (last == null) {
                return count;
            }
            return last + count;
        });
        this.total += count;
        this.totalTiers.add(tier, count);
    }
}

