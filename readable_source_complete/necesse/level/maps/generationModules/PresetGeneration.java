/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;

public class PresetGeneration {
    public final Level level;
    private final LinkedList<Rectangle> occupiedSpace = new LinkedList();

    public PresetGeneration(Level level) {
        this.level = level;
    }

    public void addOccupiedSpace(Rectangle rectangle) {
        this.occupiedSpace.add(rectangle);
    }

    public void addOccupiedSpace(int tileX, int tileY, int width, int height) {
        this.occupiedSpace.add(new Rectangle(tileX, tileY, width, height));
    }

    public LinkedList<Rectangle> getOccupiedSpace() {
        return this.occupiedSpace;
    }

    private Rectangle getPresetRectangle(Preset preset, int tileX, int tileY) {
        return new Rectangle(tileX, tileY, preset.width, preset.height);
    }

    public void applyPreset(Preset preset, int tileX, int tileY) {
        preset.applyToLevel(this.level, tileX, tileY);
        this.addOccupiedSpace(this.getPresetRectangle(preset, tileX, tileY));
    }

    public void applyPreset(Preset preset, Point pos) {
        this.applyPreset(preset, pos.x, pos.y);
    }

    public boolean isSpaceOccupied(Rectangle rectangle) {
        return this.occupiedSpace.stream().anyMatch(r -> r.intersects(rectangle));
    }

    public boolean isSpaceOccupied(int tileX, int tileY, int width, int height) {
        return this.isSpaceOccupied(new Rectangle(tileX, tileY, width, height));
    }

    public boolean canPlacePreset(Preset preset, int tileX, int tileY) {
        return !this.isSpaceOccupied(this.getPresetRectangle(preset, tileX, tileY));
    }

    public boolean canPlacePreset(Preset preset, Point pos) {
        return this.canPlacePreset(preset, pos.x, pos.y);
    }

    public Point getRandomPresetPosition(GameRandom random, Preset preset, int edgeSpace) {
        int randomX = random.getIntBetween(edgeSpace, this.level.tileWidth - preset.width - edgeSpace - 1);
        int randomY = random.getIntBetween(edgeSpace, this.level.tileHeight - preset.height - edgeSpace - 1);
        return new Point(randomX, randomY);
    }

    public Point findRandomValidPositionAndApply(GameRandom random, int attempts, Preset preset, int edgeSpace, boolean randomizeMirrorX, boolean randomizeMirrorY, boolean randomizeRotation, boolean overrideCanPlace) {
        if (randomizeMirrorX) {
            preset = PresetUtils.randomizeXMirror(preset, random);
        }
        if (randomizeMirrorY) {
            preset = PresetUtils.randomizeYMirror(preset, random);
        }
        if (randomizeRotation) {
            preset = PresetUtils.randomizeRotation(preset, random);
        }
        for (int i = 0; i < attempts; ++i) {
            Point pos = this.getRandomPresetPosition(random, preset, edgeSpace);
            if (!this.canPlacePreset(preset, pos) || !overrideCanPlace && !preset.canApplyToLevel(this.level, pos.x, pos.y)) continue;
            this.applyPreset(preset, pos);
            return pos;
        }
        return null;
    }

    public boolean findRandomValidPositionAndApply(GameRandom random, int attempts, Preset preset, int edgeSpace, boolean randomizeMirrorX, boolean randomizeMirrorY, boolean randomizeRotation) {
        return this.findRandomValidPositionAndApply(random, attempts, preset, edgeSpace, randomizeMirrorX, randomizeMirrorY, randomizeRotation, false) != null;
    }

    public boolean findRandomValidPositionAndApply(GameRandom random, int attempts, Preset preset, int edgeSpace, boolean randomizeMirror, boolean randomizeRotation) {
        return this.findRandomValidPositionAndApply(random, attempts, preset, edgeSpace, randomizeMirror, randomizeMirror, randomizeRotation, false) != null;
    }

    @Deprecated
    public boolean findRandomValidPositionAndApply(GameRandom random, Preset preset, int edgeSpace, boolean randomizeMirror, boolean randomizeRotation) {
        return this.findRandomValidPositionAndApply(random, 1, preset, edgeSpace, randomizeMirror, randomizeMirror, randomizeRotation, false) != null;
    }
}

