/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.presets.modularPresets.ModularPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageModularGeneration
extends ModularGeneration {
    public VillageModularGeneration(Level level, int cellsWidth, int cellsHeight, int cellRes, int openingSize, int openingDepth) {
        super(level, cellsWidth, cellsHeight, cellRes, openingSize, openingDepth);
    }

    @Override
    public boolean canApplyPreset(ModularPreset preset, Point cell, boolean xMirror, boolean yMirror, Point fromCell) {
        if (fromCell == null) {
            return true;
        }
        if (preset instanceof VillagePreset && ((VillagePreset)preset).isPath()) {
            for (int i = 0; i < 4; ++i) {
                ModularPreset nextPreset;
                Point nextCell = this.getNextCell(cell, i);
                if (nextCell.equals(fromCell)) continue;
                PlacedPreset pPreset = this.getPlacedPreset(nextCell);
                ModularPreset modularPreset = nextPreset = pPreset == null ? null : pPreset.preset;
                if (!(nextPreset instanceof VillagePreset) || !((VillagePreset)preset).isPath()) continue;
                return false;
            }
        }
        return true;
    }

    public Point getNextCellPlus(Point cell, int dir) {
        if ((dir %= 8) == 0) {
            return new Point(cell.x - 1, cell.y - 1);
        }
        if (dir == 1) {
            return new Point(cell.x, cell.y - 1);
        }
        if (dir == 2) {
            return new Point(cell.x + 1, cell.y - 1);
        }
        if (dir == 3) {
            return new Point(cell.x - 1, cell.y);
        }
        if (dir == 4) {
            return new Point(cell.x + 1, cell.y);
        }
        if (dir == 5) {
            return new Point(cell.x - 1, cell.y + 1);
        }
        if (dir == 6) {
            return new Point(cell.x, cell.y + 1);
        }
        if (dir == 7) {
            return new Point(cell.x + 1, cell.y + 1);
        }
        return null;
    }
}

