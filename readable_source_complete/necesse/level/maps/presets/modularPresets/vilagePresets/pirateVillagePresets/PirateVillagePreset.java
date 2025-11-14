/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class PirateVillagePreset
extends VillagePreset {
    public PirateVillagePreset(int sectionWidth, int sectionHeight, boolean isPath, GameRandom random) {
        super(sectionWidth, sectionHeight, isPath, TileRegistry.gravelID, TileRegistry.grassID, random);
    }

    public PirateVillagePreset(int sectionWidth, int sectionHeight, boolean isPath) {
        super(sectionWidth, sectionHeight, isPath, TileRegistry.gravelID, TileRegistry.grassID, null);
    }

    protected void applyRandomCoinStack(int tileX, int tileY, GameRandom random, float chance) {
        this.setObject(tileX, tileY, 0);
        this.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (random.getChance(chance)) {
                level.setObject(levelX, levelY, ObjectRegistry.getObjectID("coinstack"));
            }
            return null;
        });
    }

    protected void applyRandomCoinStack(int tileX, int tileY, GameRandom random) {
        this.applyRandomCoinStack(tileX, tileY, random, 0.6f);
    }
}

