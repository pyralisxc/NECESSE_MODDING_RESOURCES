/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import java.util.Objects;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.util.GameRandom;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.IncursionCavelingPortalPreset;
import necesse.level.maps.presets.Preset;

public class CavelingPortalCanSpawnPerk
extends IncursionPerk {
    public CavelingPortalCanSpawnPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionStructuresGenerated(PresetGeneration presets, GameRandom random, Biome biome) {
        float chance = 0.4f;
        if (presets.level.isIncursionLevel && Objects.equals(((IncursionLevel)presets.level).incursionData.getStringID(), "trial")) {
            return;
        }
        if (random.getChance(chance)) {
            Preset incursionPortalPreset = random.getOneOf(new IncursionCavelingPortalPreset());
            presets.findRandomValidPositionAndApply(random, 50, incursionPortalPreset, 5, false, false);
        }
    }
}

