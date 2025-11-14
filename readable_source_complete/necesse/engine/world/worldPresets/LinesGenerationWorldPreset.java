/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Rectangle;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.RegionPointsSet;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.LinesGeneration;

public class LinesGenerationWorldPreset {
    public final int startTileX;
    public final int startTileY;
    private final LinesGeneration linesGeneration;
    private PresetBounds presetBounds = new PresetBounds();
    private RegionPointsSet regionPointsSet = new RegionPointsSet();

    public LinesGenerationWorldPreset(int startTileX, int startTileY) {
        this.startTileX = startTileX;
        this.startTileY = startTileY;
        this.linesGeneration = new LinesGeneration(startTileX, startTileY);
        this.presetBounds.minTileX = startTileX;
        this.presetBounds.minTileY = startTileY;
        this.presetBounds.maxTileX = startTileX;
        this.presetBounds.maxTileY = startTileY;
    }

    private LinesGenerationWorldPreset(LinesGenerationWorldPreset last, LinesGeneration linesGeneration) {
        this.startTileX = last.startTileX;
        this.startTileY = last.startTileY;
        this.linesGeneration = linesGeneration;
        this.presetBounds = last.presetBounds;
        this.regionPointsSet = last.regionPointsSet;
    }

    private void updateBoundsAndRegions(LinesGeneration linesGeneration) {
        if (linesGeneration.width == 0.0f) {
            PresetBounds bounds = new PresetBounds();
            bounds.minTileX = linesGeneration.x1;
            bounds.minTileY = linesGeneration.y1;
            bounds.maxTileX = linesGeneration.x1;
            bounds.maxTileY = linesGeneration.y1;
            bounds.updateBounds(linesGeneration.x2, linesGeneration.y2);
            this.presetBounds.updateBounds(bounds);
            this.regionPointsSet.addTileBounds(bounds.minTileX, bounds.minTileY, bounds.maxTileX, bounds.maxTileY);
        } else {
            int minX = Math.min(linesGeneration.x1, linesGeneration.x2);
            int maxX = Math.max(linesGeneration.x1, linesGeneration.x2);
            int minY = Math.min(linesGeneration.y1, linesGeneration.y2);
            int maxY = Math.max(linesGeneration.y1, linesGeneration.y2);
            PresetBounds bounds = new PresetBounds();
            bounds.minTileX = (int)Math.floor((float)minX - linesGeneration.width);
            bounds.minTileY = (int)Math.floor((float)minY - linesGeneration.width);
            bounds.maxTileX = (int)Math.ceil((float)maxX + linesGeneration.width);
            bounds.maxTileY = (int)Math.ceil((float)maxY + linesGeneration.width);
            this.presetBounds.updateBounds(bounds);
            this.regionPointsSet.addTileBounds(bounds.minTileX, bounds.minTileY, bounds.maxTileX, bounds.maxTileY);
        }
    }

    public LinesGenerationWorldPreset addRandomArms(GameRandom random, int arms, float minRange, float maxRange, float minWidth, float maxWidth) {
        int angle = random.nextInt(360);
        int anglePerArm = 360 / arms;
        for (int i = 0; i < arms; ++i) {
            float range = random.getFloatBetween(minRange, maxRange);
            float width = random.getFloatBetween(minWidth, maxWidth);
            this.addArm(angle += random.getIntOffset(anglePerArm, anglePerArm / 2), range, width);
        }
        return this;
    }

    public LinesGenerationWorldPreset addArm(float angle, float range, float width) {
        LinesGeneration next = this.linesGeneration.addArm(angle, range, width);
        this.updateBoundsAndRegions(next);
        return new LinesGenerationWorldPreset(this, next);
    }

    public boolean isWithinPresetRegionBounds(LevelPresetsRegion presetsRegion) {
        return this.presetBounds.minTileX >= presetsRegion.worldRegion.startTileX && this.presetBounds.maxTileX < presetsRegion.worldRegion.startTileX + presetsRegion.worldRegion.tileWidth && this.presetBounds.minTileY >= presetsRegion.worldRegion.startTileY && this.presetBounds.maxTileY < presetsRegion.worldRegion.startTileY + presetsRegion.worldRegion.tileHeight;
    }

    public CellAutomaton doCellularAutomaton(GameRandom random) {
        return this.linesGeneration.doCellularAutomaton(random);
    }

    public PointHashSet getDiamondPoints() {
        return this.linesGeneration.getDiamondPoints();
    }

    public RegionPointsSet getRegionPointsSet() {
        return this.regionPointsSet;
    }

    public Rectangle getOccupiedTileRectangle() {
        return new Rectangle(this.presetBounds.minTileX, this.presetBounds.minTileY, this.presetBounds.maxTileX - this.presetBounds.minTileX + 1, this.presetBounds.maxTileY - this.presetBounds.minTileY + 1);
    }

    public Iterable<LinesGeneration> getLines() {
        return this.linesGeneration.getLines();
    }

    private static class PresetBounds {
        public int minTileX;
        public int minTileY;
        public int maxTileX;
        public int maxTileY;

        private PresetBounds() {
        }

        private void updateBounds(int tileX, int tileY) {
            if (tileX < this.minTileX) {
                this.minTileX = tileX;
            } else if (tileX > this.maxTileX) {
                this.maxTileX = tileX;
            }
            if (tileY < this.minTileY) {
                this.minTileY = tileY;
            } else if (tileY > this.maxTileY) {
                this.maxTileY = tileY;
            }
        }

        private void updateBounds(PresetBounds other) {
            if (other.minTileX < this.minTileX) {
                this.minTileX = other.minTileX;
            }
            if (other.maxTileX > this.maxTileX) {
                this.maxTileX = other.maxTileX;
            }
            if (other.minTileY < this.minTileY) {
                this.minTileY = other.minTileY;
            }
            if (other.maxTileY > this.maxTileY) {
                this.maxTileY = other.maxTileY;
            }
        }
    }
}

