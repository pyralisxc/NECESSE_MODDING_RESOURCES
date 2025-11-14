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
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.incursionPresets.ArcanicInvadersPreset1;
import necesse.level.maps.presets.incursionPresets.ArcanicInvadersPreset2;
import necesse.level.maps.presets.incursionPresets.ArcanicInvadersPreset3;

public class ArcanicInvadersPerk
extends IncursionPerk {
    public ArcanicInvadersPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionStructuresGenerated(PresetGeneration presets, GameRandom random, Biome biome) {
        float chance = 0.4f;
        if (presets.level.isIncursionLevel && Objects.equals(((IncursionLevel)presets.level).incursionData.getStringID(), "trial")) {
            return;
        }
        if (random.getChance(chance)) {
            Preset arcanicInvaderPreset = random.getOneOf(new ArcanicInvadersPreset1(random), new ArcanicInvadersPreset2(random), new ArcanicInvadersPreset3(random));
            presets.findRandomValidPositionAndApply(random, 50, arcanicInvaderPreset, 5, true, false);
        }
    }
}

