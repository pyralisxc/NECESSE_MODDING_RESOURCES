/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldApplyPredicate;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class WorldPresetTester {
    public final int width;
    public final int height;
    public LinkedList<WorldApplyPredicate> applyPredicates = new LinkedList();

    public WorldPresetTester(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean canApply(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
        if (tileX < presetsRegion.worldRegion.startTileX || tileY < presetsRegion.worldRegion.startTileY || tileX + this.width > presetsRegion.worldRegion.endTileX || tileY + this.height > presetsRegion.worldRegion.endTileY) {
            return false;
        }
        for (WorldApplyPredicate applyPredicate : this.applyPredicates) {
            if (applyPredicate.canApply(preset, presetsRegion, generatorStack, tileX, tileY)) continue;
            return false;
        }
        return true;
    }

    public WorldPresetTester rotate(PresetRotation rotation) {
        if (rotation == null) {
            return this;
        }
        Point dim = PresetUtils.getRotatedPoint(this.width, this.height, 0, 0, rotation);
        WorldPresetTester preset = new WorldPresetTester(Math.abs(dim.x), Math.abs(dim.y));
        for (WorldApplyPredicate applyPredicate : this.applyPredicates) {
            preset.applyPredicates.add(applyPredicate.rotate(rotation, this.width, this.height));
        }
        return preset;
    }

    public WorldPresetTester mirrorX() {
        WorldPresetTester preset = new WorldPresetTester(this.width, this.height);
        for (WorldApplyPredicate applyPredicate : this.applyPredicates) {
            preset.applyPredicates.add(applyPredicate.mirrorX(preset.width));
        }
        return preset;
    }

    public WorldPresetTester mirrorY() {
        WorldPresetTester preset = new WorldPresetTester(this.width, this.height);
        for (WorldApplyPredicate applyPredicate : this.applyPredicates) {
            preset.applyPredicates.add(applyPredicate.mirrorY(preset.height));
        }
        return preset;
    }

    public WorldPresetTester addApplyPredicate(WorldApplyPredicate predicate) {
        this.applyPredicates.add(predicate);
        return this;
    }

    public Dimension getSize() {
        return new Dimension(this.width, this.height);
    }
}

