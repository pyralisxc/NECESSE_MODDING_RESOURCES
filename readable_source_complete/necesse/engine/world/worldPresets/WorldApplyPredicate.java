/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.presets.PresetRotation;

public interface WorldApplyPredicate {
    public boolean canApply(WorldPreset var1, LevelPresetsRegion var2, BiomeGeneratorStack var3, int var4, int var5);

    public WorldApplyPredicate mirrorX(int var1);

    public WorldApplyPredicate mirrorY(int var1);

    public WorldApplyPredicate rotate(PresetRotation var1, int var2, int var3);
}

