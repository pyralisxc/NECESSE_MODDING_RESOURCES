/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Point;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.engine.world.worldPresets.WorldPresetTester;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class GenerationPresetTile
extends Point {
    protected WorldPresetTester tester;
    protected boolean xMirrored;
    protected boolean yMirrored;
    protected PresetRotation rotation;

    private GenerationPresetTile(int x, int y, WorldPresetTester tester, boolean xMirrored, boolean yMirrored, PresetRotation rotation) {
        super(x, y);
        this.tester = tester;
        this.xMirrored = xMirrored;
        this.yMirrored = yMirrored;
        this.rotation = rotation;
    }

    public GenerationPresetTile(int x, int y, WorldPresetTester tester) {
        this(x, y, tester, false, false, null);
    }

    public boolean canApply(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        return this.tester.canApply(preset, presetsRegion, generatorStack, this.x, this.y);
    }

    public GenerationPresetTile rotate(PresetRotation rotation, int pivotX, int pivotY) {
        Point newPosOffset = PresetUtils.getRotatedPoint(this.tester.width, this.tester.height, pivotX, pivotY, rotation);
        if (rotation == PresetRotation.CLOCKWISE) {
            newPosOffset.y -= this.tester.width;
        } else if (rotation == PresetRotation.ANTI_CLOCKWISE) {
            newPosOffset.x -= this.tester.height;
        }
        return new GenerationPresetTile(this.x + newPosOffset.x, this.y + newPosOffset.y, this.tester.rotate(rotation), this.xMirrored, this.yMirrored, PresetRotation.addRotations(this.rotation, rotation));
    }

    public GenerationPresetTile mirrorX(int mirrorTileX) {
        int newXOffset = mirrorTileX - (this.tester.width - mirrorTileX);
        return new GenerationPresetTile(this.x + newXOffset, this.y, this.tester.mirrorX(), !this.xMirrored, this.yMirrored, this.rotation);
    }

    public GenerationPresetTile mirrorY(int mirrorTileY) {
        int newYOffset = mirrorTileY - (this.tester.height - mirrorTileY);
        return new GenerationPresetTile(this.x, this.y + newYOffset, this.tester.mirrorY(), this.xMirrored, !this.yMirrored, this.rotation);
    }

    public GenerationPresetTile rotate(PresetRotation rotation) {
        return new GenerationPresetTile(this.x, this.y, this.tester.rotate(rotation), this.xMirrored, this.yMirrored, PresetRotation.addRotations(this.rotation, rotation));
    }

    public GenerationPresetTile mirrorX() {
        return new GenerationPresetTile(this.x, this.y, this.tester.mirrorX(), !this.xMirrored, this.yMirrored, this.rotation);
    }

    public GenerationPresetTile mirrorY() {
        return new GenerationPresetTile(this.x, this.y, this.tester.mirrorY(), this.xMirrored, !this.yMirrored, this.rotation);
    }

    public Preset modifyPreset(Preset preset) throws PresetMirrorException, PresetRotateException {
        if (this.xMirrored) {
            preset = preset.mirrorX();
        }
        if (this.yMirrored) {
            preset = preset.mirrorY();
        }
        if (this.rotation != null) {
            preset = preset.rotate(this.rotation);
        }
        return preset;
    }
}

