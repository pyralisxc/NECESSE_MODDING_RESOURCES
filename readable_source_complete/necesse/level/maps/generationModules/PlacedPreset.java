/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class PlacedPreset {
    public final ModularGeneration mg;
    public final Point cell;
    public final boolean xMirror;
    public final boolean yMirror;
    public final ModularPreset preset;
    public final int x;
    public final int y;
    public final int realX;
    public final int realY;
    public final PlacedPreset from;
    public int heuristic;
    private boolean[] open;
    public final Preset replacedLevel;

    public PlacedPreset(ModularGeneration mg, Point cell, ModularPreset preset, int x, int y, boolean xMirror, boolean yMirror, PlacedPreset from, Preset replacedLevel) {
        this.mg = mg;
        this.cell = cell;
        this.preset = preset;
        this.realX = x;
        this.realY = y;
        if (xMirror) {
            x = Math.abs(x - (preset.sectionWidth - 1));
        }
        if (yMirror) {
            y = Math.abs(y - (preset.sectionHeight - 1));
        }
        this.x = x;
        this.y = y;
        this.xMirror = xMirror;
        this.yMirror = yMirror;
        this.from = from;
        this.heuristic = from != null ? from.heuristic + 1 : 0;
        this.open = new boolean[4];
        this.replacedLevel = replacedLevel;
    }

    public boolean isPresetOpenTop() {
        int dir = this.yMirror ? 2 : 0;
        return this.preset.isOpen(this.x, this.y, dir);
    }

    public boolean isPresetOpenBot() {
        int dir = this.yMirror ? 0 : 2;
        return this.preset.isOpen(this.x, this.y, dir);
    }

    public boolean isPresetOpenRight() {
        int dir = this.xMirror ? 3 : 1;
        return this.preset.isOpen(this.x, this.y, dir);
    }

    public boolean isPresetOpenLeft() {
        int dir = this.xMirror ? 1 : 3;
        return this.preset.isOpen(this.x, this.y, dir);
    }

    public boolean isPresetOpenDir(int dir) {
        if ((dir %= 4) == 0) {
            return this.isPresetOpenTop();
        }
        if (dir == 1) {
            return this.isPresetOpenRight();
        }
        if (dir == 2) {
            return this.isPresetOpenBot();
        }
        if (dir == 3) {
            return this.isPresetOpenLeft();
        }
        return false;
    }

    public static int getCellsDir(Point cell1, Point cell2) {
        if (cell1.x != cell2.x && cell1.y != cell2.y) {
            throw new IllegalArgumentException("Cells aren't adjacent");
        }
        if (cell1.x != cell2.x) {
            return cell1.x < cell2.x ? 1 : 3;
        }
        return cell1.y < cell2.y ? 2 : 0;
    }

    public void openDirs(Level level, PlacedPreset from, int xOffset, int yOffset, GameRandom random) {
        if (from != null) {
            int toDir;
            int fromDir;
            if (this.heuristic > from.heuristic + 1) {
                this.heuristic = from.heuristic + 1;
                if (this.from != null) {
                    this.from.fixHeuristic(this.heuristic + 1);
                }
            }
            if (!from.dirOpen(fromDir = PlacedPreset.getCellsDir(from.cell, this.cell))) {
                from.openDir(level, xOffset, yOffset, fromDir, random);
            }
            if (!this.dirOpen(toDir = PlacedPreset.getCellsDir(this.cell, from.cell))) {
                this.openDir(level, xOffset, yOffset, toDir, random);
            }
        }
    }

    public void fixHeuristic(int next) {
        if (this.heuristic > next) {
            this.heuristic = next;
            if (this.from != null) {
                this.from.fixHeuristic(next + 1);
            }
        }
    }

    public void openDirs(Level level, int xOffset, int yOffset, GameRandom random) {
        this.openDirs(level, this.from, xOffset, yOffset, random);
    }

    private void openDir(Level level, int xOffset, int yOffset, int dir, GameRandom random) {
        this.preset.openLevel(level, this.cell.x, this.cell.y, xOffset, yOffset, dir, random, this.mg.cellRes);
        this.open[dir] = true;
    }

    private boolean dirOpen(int dir) {
        return this.open[dir];
    }
}

