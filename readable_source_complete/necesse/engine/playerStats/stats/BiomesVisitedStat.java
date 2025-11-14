/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.biomes.Biome;

public class BiomesVisitedStat
extends GameStat {
    protected HashSet<String> visitedBiomeStringIDs = new HashSet();
    protected HashSet<String> statsOnlyBiomeStringIDs = new HashSet();
    protected HashSet<String> dirtyVisitedBiomeStringIDs = new HashSet();
    protected int islandsDiscovered;
    protected int islandsVisited;

    public BiomesVisitedStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyVisitedBiomeStringIDs.clear();
    }

    protected boolean setBiomeKnown(String biomeStringID, boolean known, boolean updateSteam) {
        int biomeID = LevelSave.getMigratedBiomeID(biomeStringID, false);
        if (biomeID != -1) {
            boolean prevStat = this.visitedBiomeStringIDs.contains(biomeStringID);
            if (prevStat == known) {
                return false;
            }
            if (known) {
                this.visitedBiomeStringIDs.add(biomeStringID);
                if (BiomeRegistry.doesBiomeCountInStats(biomeID)) {
                    this.statsOnlyBiomeStringIDs.add(biomeStringID);
                }
            } else {
                this.visitedBiomeStringIDs.remove(biomeStringID);
                this.statsOnlyBiomeStringIDs.remove(biomeStringID);
            }
            if (updateSteam) {
                this.updatePlatform();
            }
            this.dirtyVisitedBiomeStringIDs.add(biomeStringID);
            this.markImportantDirty();
            return true;
        }
        return false;
    }

    public boolean markBiomeVisited(Biome biome) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        return this.setBiomeKnown(biome.getStringID(), true, true);
    }

    public boolean isBiomeVisited(Biome biome) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.visitedBiomeStringIDs.contains(biome.getStringID());
    }

    public Stream<String> streamVisitedBiomes() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.visitedBiomeStringIDs.stream();
    }

    public void forEachVisitedStatsBiome(Consumer<String> action) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        this.statsOnlyBiomeStringIDs.forEach(action);
    }

    public void forEachVisitedBiome(Consumer<String> action) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        this.visitedBiomeStringIDs.forEach(action);
    }

    public int getTotalVisitedUniqueBiomes() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.visitedBiomeStringIDs.size();
    }

    public int getTotalVisitedUniqueStatsBiomes() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.visitedBiomeStringIDs.size();
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof BiomesVisitedStat) {
            BiomesVisitedStat other = (BiomesVisitedStat)stat;
            for (String visitedBiomeStringID : other.visitedBiomeStringIDs) {
                this.setBiomeKnown(visitedBiomeStringID, true, true);
            }
        }
    }

    @Override
    public void resetCombine() {
        this.visitedBiomeStringIDs.clear();
        this.statsOnlyBiomeStringIDs.clear();
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            return;
        }
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
        this.islandsDiscovered = stats.islands_discovered;
        this.islandsVisited = stats.islands_visited;
    }

    @Override
    public void addSaveData(SaveData save) {
        save.addStringHashSet("visitedBiomes", this.visitedBiomeStringIDs);
    }

    @Override
    public void applyLoadData(LoadData save) {
        HashSet<String> loadedStringIDs = save.getStringHashSet("visitedBiomes", new HashSet<String>(), false);
        this.visitedBiomeStringIDs = new HashSet(loadedStringIDs.size());
        this.statsOnlyBiomeStringIDs = new HashSet(loadedStringIDs.size());
        for (String loadedStringID : loadedStringIDs) {
            int biomeID = LevelSave.getMigratedBiomeID(loadedStringID, false);
            if (biomeID == -1) continue;
            this.visitedBiomeStringIDs.add(loadedStringID);
            if (!BiomeRegistry.doesBiomeCountInStats(biomeID)) continue;
            this.statsOnlyBiomeStringIDs.add(loadedStringID);
        }
        for (LoadData component : save.getLoadData()) {
            if (!component.isData() || component.getName().equals("visitedBiomes")) continue;
            try {
                int biomeID = LevelSave.getMigratedBiomeID(component.getName(), false);
                if (biomeID != -1) {
                    int amount = Integer.parseInt(component.getData());
                    if (amount <= 0) continue;
                    this.setBiomeKnown(BiomeRegistry.getBiomeStringID(biomeID), true, false);
                    continue;
                }
                GameLog.warn.println("Could not load biomes known stat stringID: " + component.getName());
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not load biomes known stat number: " + component.getData());
            }
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.visitedBiomeStringIDs.size());
        for (String biomeStringID : this.visitedBiomeStringIDs) {
            writer.putNextShortUnsigned(BiomeRegistry.getBiomeID(biomeStringID));
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        this.visitedBiomeStringIDs.clear();
        this.statsOnlyBiomeStringIDs.clear();
        for (int i = 0; i < amount; ++i) {
            int biomeID = reader.getNextShortUnsigned();
            String biomeStringID = BiomeRegistry.getBiomeStringID(biomeID);
            this.setBiomeKnown(biomeStringID, true, true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyVisitedBiomeStringIDs.size());
        for (String biomeStringID : this.dirtyVisitedBiomeStringIDs) {
            writer.putNextShortUnsigned(BiomeRegistry.getBiomeID(biomeStringID));
            writer.putNextBoolean(this.visitedBiomeStringIDs.contains(biomeStringID));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int biomeID = reader.getNextShortUnsigned();
            boolean visited = reader.getNextBoolean();
            String biomeStringID = BiomeRegistry.getBiomeStringID(biomeID);
            this.setBiomeKnown(biomeStringID, visited, true);
        }
    }
}

