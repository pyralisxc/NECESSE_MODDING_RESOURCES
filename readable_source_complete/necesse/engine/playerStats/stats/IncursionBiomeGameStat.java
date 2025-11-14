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
import necesse.engine.world.worldData.incursions.IncursionBiomeStats;
import necesse.engine.world.worldData.incursions.IncursionDataStats;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;

public class IncursionBiomeGameStat
extends GameStat {
    protected IncursionBiomeStats biomes = new IncursionBiomeStats();

    public IncursionBiomeGameStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof IncursionBiomeGameStat) {
            IncursionBiomeGameStat other = (IncursionBiomeGameStat)stat;
            this.biomes.combine(other.biomes);
            if (other.biomes.getTotal() > 0) {
                this.updatePlatform();
                this.markDirty();
            }
        }
    }

    @Override
    public void resetCombine() {
        this.biomes.reset();
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            return;
        }
        Platform.getStatsProvider().setStat(this.stringID, this.biomes.getTotal());
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
        this.biomes.loadPlatformTotal(stats.opened_incursions);
    }

    @Override
    public void addSaveData(SaveData save) {
        this.biomes.addSaveData(save);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.biomes.applyLoadData(save);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        this.biomes.setupContentPacket(writer);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.biomes.applyContentPacket(reader);
    }

    public int getTotal() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.biomes.getTotal();
    }

    public int getTotal(IncursionBiome biome) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.biomes.getTotal(biome);
    }

    public int getTotal(String biomeStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.biomes.getTotal(biomeStringID);
    }

    public int getTotal(int biomeID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.biomes.getTotal(biomeID);
    }

    public IncursionDataStats getData(IncursionBiome biome) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.biomes.getData(biome);
    }

    public IncursionDataStats getData(String biomeStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.biomes.getData(biomeStringID);
    }

    public IncursionDataStats getData(int biomeID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.biomes.getData(biomeID);
    }

    public int getTotalTiersBelow(int tier, boolean inclusive) {
        return this.biomes.getTotalTiersBelow(tier, inclusive);
    }

    public int getTotalTiersAbove(int tier, boolean inclusive) {
        return this.biomes.getTotalTiersAbove(tier, inclusive);
    }

    public int getTotalTiers(int tier) {
        return this.biomes.getTotalTiers(tier);
    }

    public void add(BiomeMissionIncursionData incursion) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.biomes.add(incursion);
        this.updatePlatform();
        this.markDirty();
    }
}

