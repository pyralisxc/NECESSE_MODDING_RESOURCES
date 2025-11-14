/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.incursionPresets.GraveyardMegaUpgradeShardsClusterPreset;

public class MegaUpgradeShardVeinPerk
extends IncursionPerk {
    public MegaUpgradeShardVeinPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onGenerateUpgradeAndAlchemyVeins(CaveGeneration cg, String upgradeShardID, String alchemyShardID, GameRandom random) {
        super.onGenerateUpgradeAndAlchemyVeins(cg, upgradeShardID, alchemyShardID, random);
        if (random.getChance(0.25f)) {
            cg.generateGuaranteedOreVeins(65, 65, 65, ObjectRegistry.getObjectID(upgradeShardID));
        }
    }

    @Override
    public void onIncursionStructuresGenerated(PresetGeneration presets, GameRandom random, Biome biome) {
        if (biome == BiomeRegistry.GRAVEYARD && random.getChance(0.25f)) {
            presets.findRandomValidPositionAndApply(random, 50, new GraveyardMegaUpgradeShardsClusterPreset(), 5, true, true);
        }
    }
}

