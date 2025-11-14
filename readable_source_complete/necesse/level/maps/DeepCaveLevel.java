/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.levelEvent.SpiritCorruptedLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.level.maps.BiomeGeneratorStackLevel;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.regionSystem.Region;

public class DeepCaveLevel
extends BiomeGeneratorStackLevel {
    public static int CURSED_CRONE_DEATH_PREVENT_SPIRIT_CORRUPTED_SECONDS = 3600;
    public static int MIN_SPIRIT_CORRUPTED_COOLDOWN_SECONDS = 30;
    public static int MAX_SPIRIT_CORRUPTED_COOLDOWN_SECONDS = 45;
    public static int SPIRIT_CORRUPTED_DURATION_SECONDS = 25;
    public long nextSpiritCorruptedTime;

    public DeepCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public DeepCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity, int seed) {
        super(identifier, width, height, worldEntity, seed);
        this.isCave = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateRegion(Region region) {
        super.generateRegion(region);
        this.startDirtyRegionTracking();
        int presetGenerationUniqueID = this.getWorldEntity().startPresetGenerationInRegion(region, this.seed);
        try {
            TreeSet<Biome> foundBiomes = new TreeSet<Biome>(Comparator.comparingInt(Biome::getID));
            for (int regionTileX = 0; regionTileX < region.tileLayer.region.tileWidth; ++regionTileX) {
                for (int regionTileY = 0; regionTileY < region.tileLayer.region.tileHeight; ++regionTileY) {
                    int tileX = regionTileX + region.tileXOffset;
                    int tileY = regionTileY + region.tileYOffset;
                    Biome biome = this.generatorStack.getExpensiveBiome(tileX, tileY);
                    Biome spreadBiome = this.generatorStack.getSpreadBiome(tileX, tileY);
                    region.biomeLayer.setBiomeByRegion(regionTileX, regionTileY, biome.getID());
                    foundBiomes.add(spreadBiome);
                    boolean isLava = this.generatorStack.isDeepCaveLava(tileX, tileY);
                    if (isLava) {
                        region.tileLayer.setTileByRegion(regionTileX, regionTileY, spreadBiome.getGenerationDeepCaveLavaTileID());
                        continue;
                    }
                    region.tileLayer.setTileByRegion(regionTileX, regionTileY, spreadBiome.getGenerationDeepCaveTileID());
                    if (!this.generatorStack.isDeepCaveRock(tileX, tileY)) continue;
                    region.objectLayer.setObjectByRegion(ObjectLayerRegistry.BASE_LAYER, regionTileX, regionTileY, spreadBiome.getGenerationDeepCaveRockObjectID());
                }
            }
            for (Biome biome : foundBiomes) {
                biome.generateRegionDeepCaveTerrain(region, this.generatorStack, this.generatorStack.getNewRegionRandom(region));
            }
        }
        finally {
            this.getWorldEntity().runPresetGenerationInRegion(presetGenerationUniqueID, region, this.seed);
            this.removeDirtyRegion(region.regionX, region.regionY);
        }
    }

    @Override
    public void onRegionGenerated(Region region, boolean skipGenerateForced) {
        super.onRegionGenerated(region, skipGenerateForced);
        region.checkGenerationValid();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("nextSpiritCorruptedTime", this.nextSpiritCorruptedTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.nextSpiritCorruptedTime = save.getLong("nextSpiritCorruptedTime", 0L, false);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.nextSpiritCorruptedTime == 0L) {
            this.nextSpiritCorruptedTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(MIN_SPIRIT_CORRUPTED_COOLDOWN_SECONDS, MAX_SPIRIT_CORRUPTED_COOLDOWN_SECONDS) * 1000L;
        }
        if (this.nextSpiritCorruptedTime <= this.getTime()) {
            this.nextSpiritCorruptedTime = 0L;
            if (!this.buffManager.getModifier(LevelModifiers.SPIRIT_CORRUPTED).booleanValue()) {
                this.entityManager.events.add(new SpiritCorruptedLevelEvent(this.getTime() + (long)SPIRIT_CORRUPTED_DURATION_SECONDS * 1000L));
            }
        }
    }

    @Override
    public void onMobDied(Mob mob, Attacker attacker, HashSet<Attacker> attackers) {
        super.onMobDied(mob, attacker, attackers);
        if (mob.getStringID().equals("thecursedcrone")) {
            System.out.println("AGHDASGHDJUHJU!!!");
            this.nextSpiritCorruptedTime = Math.max(this.nextSpiritCorruptedTime, this.getTime() + (long)CURSED_CRONE_DEATH_PREVENT_SPIRIT_CORRUPTED_SECONDS * 1000L);
        }
    }
}

