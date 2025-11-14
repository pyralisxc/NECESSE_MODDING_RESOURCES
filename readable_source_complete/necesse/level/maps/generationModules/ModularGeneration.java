/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class ModularGeneration {
    Level level;
    public GameRandom random;
    private final ArrayList<ModularPreset> presets;
    private final HashMap<ModularPreset, Integer> presetUsage;
    private final HashMap<ModularPreset, Integer> presetMaxUsage;
    private final ArrayList<ModularPreset> fillPresets;
    private final HashMap<ModularPreset, Integer> fillPresetUsage;
    private final HashMap<ModularPreset, Integer> fillPresetMaxUsage;
    protected ModularPreset startPreset;
    public final int cellsWidth;
    public final int cellsHeight;
    public final int cellRes;
    public final int openingSize;
    public final int openingDepth;
    private ArrayList<Point> openCells;
    private final HashMap<Point, PlacedPreset> placedPresets;
    public boolean allowRoundTrips;
    private final int traceStart = Thread.currentThread().getStackTrace().length;
    public boolean printDebug = false;

    public ModularGeneration(Level level, int cellsWidth, int cellsHeight, int cellRes, int openingSize, int openingDepth) {
        this.level = level;
        this.cellsWidth = cellsWidth;
        this.cellsHeight = cellsHeight;
        this.cellRes = cellRes;
        this.openingSize = openingSize;
        this.openingDepth = openingDepth;
        this.random = new GameRandom(level.getSeed());
        this.presets = new ArrayList();
        this.placedPresets = new HashMap();
        this.presetUsage = new HashMap();
        this.presetMaxUsage = new HashMap();
        this.fillPresets = new ArrayList();
        this.fillPresetUsage = new HashMap();
        this.fillPresetMaxUsage = new HashMap();
        this.allowRoundTrips = true;
    }

    public static int getCellsSize(int size, int cellRes) {
        return (size - 10) / cellRes;
    }

    public void generateModularGeneration() {
        this.generateModularGeneration(0, 0);
    }

    public void generateModularGeneration(int xOffset, int yOffset) {
        this.initGeneration(xOffset, yOffset);
        while (this.canTickGeneration()) {
            this.tickGeneration(xOffset, yOffset);
        }
        this.endGeneration();
    }

    public void generateModularGeneration(int xOffset, int yOffset, int tickIterations) {
        this.initGeneration(xOffset, yOffset);
        this.tickGeneration(xOffset, yOffset, tickIterations);
        this.endGeneration();
    }

    public boolean canTickGeneration() {
        return this.openCells.size() != 0;
    }

    public Point initGeneration(int xOffset, int yOffset) {
        this.placedPresets.clear();
        this.random = new GameRandom(this.level.getSeed(), false);
        this.openCells = new ArrayList();
        if (this.startPreset == null) {
            throw new NullPointerException("Start preset cannot be null");
        }
        Point spawnCell = this.getStartCell();
        this.debug("Startcell: " + spawnCell.x + ", " + spawnCell.y);
        this.applyPreset(this.startPreset, spawnCell, false, false, xOffset, yOffset, null);
        return spawnCell;
    }

    public void tickGeneration(int xOffset, int yOffset, int iterations) {
        if (iterations < 0) {
            while (this.canTickGeneration()) {
                this.tickGeneration(xOffset, yOffset);
            }
        } else {
            for (int i = 0; i < iterations && this.canTickGeneration(); ++i) {
                this.tickGeneration(xOffset, yOffset);
            }
        }
    }

    public boolean canTickFillGeneration() {
        return this.fillPresets.size() != 0;
    }

    public void tickFillGeneration(int xOffset, int yOffset, int iterations) {
        if (iterations < 0) {
            while (this.canTickFillGeneration()) {
                this.tickFillGeneration(xOffset, yOffset);
            }
        } else {
            for (int i = 0; i < iterations && this.canTickFillGeneration(); ++i) {
                this.tickFillGeneration(xOffset, yOffset);
            }
        }
    }

    public void endGeneration() {
        this.presets.clear();
        this.presetUsage.clear();
        this.presetMaxUsage.clear();
        this.fillPresets.clear();
        this.fillPresetUsage.clear();
        this.fillPresetMaxUsage.clear();
        this.startPreset = null;
    }

    public void tickFillGeneration(int xOffset, int yOffset) {
        if (!this.canTickFillGeneration()) {
            return;
        }
        this.debug("Ticking fill with pool of " + this.fillPresets.size() + " presets.");
        ModularPreset p = this.fillPresets.get(this.random.nextInt(this.fillPresets.size()));
        this.debug("Checking random fill preset " + p.getClass().getSimpleName());
        if (this.fillPresetMaxUsage.get(p) > 0 && this.fillPresetUsage.get(p) >= this.fillPresetMaxUsage.get(p)) {
            this.removeAllFillPresets(p);
            this.debug("Preset hit max usage.");
            this.tickFillGeneration(xOffset, yOffset);
            return;
        }
        int checkWidth = this.cellsWidth - p.width;
        int checkHeight = this.cellsHeight - p.height;
        if (checkWidth < 1 || checkHeight < 1) {
            this.removeAllFillPresets(p);
            this.debug("Preset too big for the grid.");
            this.tickFillGeneration(xOffset, yOffset);
            return;
        }
        this.debug("Found random fill preset " + p.getClass().getSimpleName());
        boolean placed = false;
        boolean xMirror = this.random.nextBoolean();
        boolean yMirror = this.random.nextBoolean();
        int randomXInt = Math.abs(this.random.nextInt());
        int randomYInt = Math.abs(this.random.nextInt());
        for (int cellX = 0; cellX < this.cellsWidth - p.width; ++cellX) {
            int randomX = (randomXInt + cellX) % checkWidth;
            for (int cellY = 0; cellY < this.cellsWidth - p.height; ++cellY) {
                int randomY = (randomYInt + cellY) % checkHeight;
                Point placeCell = new Point(randomX, randomY);
                if (!this.applyFillPreset(p, placeCell, xMirror, yMirror, xOffset, yOffset)) continue;
                this.debug("Found place location for " + p.getClass().getSimpleName() + " at (" + placeCell.x + ", " + placeCell.y + ")");
                this.fillPresetUsage.put(p, this.fillPresetUsage.get(p) + 1);
                placed = true;
                break;
            }
            if (placed) break;
        }
        if (!placed) {
            this.debug("Could not find any location for " + p.getClass().getSimpleName());
            this.removeAllFillPresets(p);
        }
    }

    private void removeAllFillPresets(ModularPreset p) {
        while (this.fillPresets.remove(p)) {
        }
    }

    public void tickGeneration(int xOffset, int yOffset) {
        if (this.openCells.size() == 0) {
            return;
        }
        Point current = this.openCells.get(this.openCells.size() - 1);
        this.debug("Current cell: (" + current.x + ", " + current.y + ") out of " + this.openCells.size());
        boolean found = false;
        boolean close = true;
        boolean firstPreset = true;
        for (int j = 0; j < this.presets.size(); ++j) {
            int lastIndex;
            int rPreset = this.random.nextInt(this.presets.size());
            int randomIndex = (rPreset + j) % this.presets.size();
            ModularPreset p = this.presets.get(randomIndex);
            if (this.presetMaxUsage.get(p) > 0 && this.presetUsage.get(p) >= this.presetMaxUsage.get(p)) {
                while (this.presets.remove(p)) {
                }
                j = 0;
                continue;
            }
            if (!firstPreset && p == this.presets.get(lastIndex = (rPreset + j - 1) % this.presets.size())) continue;
            firstPreset = false;
            this.debug("Checking preset " + p.getClass().getSimpleName());
            int next = this.random.nextInt(4);
            for (int i = 0; i < 4; ++i) {
                int dir = (i + next) % 4;
                this.debug("Checking dir " + dir);
                Point nextCell = this.getNextCell(current, dir);
                if (this.placedPresets.containsKey(nextCell) || !this.isCellOpen(current, dir)) continue;
                this.debug("Found opening: (" + nextCell.x + ", " + nextCell.y + ")");
                int open1 = p.getRandomOpenDir(this.random.nextInt(), dir);
                int open2 = p.getRandomOpenDir(this.random.nextInt(), dir + 2);
                if (open1 != -1 || open2 != -1) {
                    boolean mirror;
                    Point placeCell;
                    this.debug("Found opening in preset " + open1 + ", " + open2);
                    if (dir == 0 || dir == 2) {
                        placeCell = new Point(nextCell.x, nextCell.y);
                        boolean bl = mirror = open1 != -1;
                        if (mirror && open2 != -1) {
                            mirror = this.random.nextBoolean();
                        }
                        if (dir == 0) {
                            placeCell.y -= p.sectionHeight - 1;
                        }
                        placeCell.x = placeCell.x - (!mirror ? open2 : open1);
                        if (this.applyPreset(p, placeCell, false, mirror, xOffset, yOffset, current)) {
                            this.getPlacedPreset(nextCell).openDirs(this.level, xOffset, yOffset, this.random);
                            close = false;
                            found = true;
                        }
                    } else if (dir == 1 || dir == 3) {
                        placeCell = new Point(nextCell.x, nextCell.y);
                        boolean bl = mirror = open1 != -1;
                        if (mirror && open2 != -1) {
                            mirror = this.random.nextBoolean();
                        }
                        if (dir == 3) {
                            placeCell.x -= p.sectionWidth - 1;
                        }
                        placeCell.y = placeCell.y - (!mirror ? open2 : open1);
                        if (this.applyPreset(p, placeCell, mirror, false, xOffset, yOffset, current)) {
                            this.getPlacedPreset(nextCell).openDirs(this.level, xOffset, yOffset, this.random);
                            close = false;
                            found = true;
                        }
                    }
                }
                if (found) break;
            }
            if (found) break;
        }
        if (close) {
            this.debug("Closed cell: (" + current.x + ", " + current.y + ")");
            this.openCells.remove(current);
        }
    }

    public void openCell(Point cell) {
        if (this.openCells.contains(cell)) {
            return;
        }
        this.openCells.add(cell);
    }

    public void removePlacedPreset(PlacedPreset preset) {
        this.placedPresets.remove(preset.cell);
    }

    public final void addPreset(ModularPreset preset, int tickets) {
        this.addPreset(preset, tickets, -1);
    }

    public void addPreset(ModularPreset preset, int tickets, int maxUsage) {
        for (int i = 0; i < tickets; ++i) {
            this.presets.add(preset);
        }
        this.presetUsage.put(preset, 0);
        this.presetMaxUsage.put(preset, maxUsage);
    }

    public void removeAllPresets(ModularPreset p) {
        while (this.presets.remove(p)) {
        }
    }

    public final void addFillPreset(ModularPreset preset, int tickets) {
        this.addFillPreset(preset, tickets, -1);
    }

    public void addFillPreset(ModularPreset preset, int tickets, int maxUsage) {
        for (int i = 0; i < tickets; ++i) {
            this.fillPresets.add(preset);
        }
        this.fillPresetUsage.put(preset, 0);
        this.fillPresetMaxUsage.put(preset, maxUsage);
    }

    public void clearPresets() {
        this.presets.clear();
    }

    public void setStartPreset(ModularPreset preset) {
        this.startPreset = preset;
    }

    public boolean applyPreset(ModularPreset preset, Point cell, boolean xMirror, boolean yMirror, int xOffset, int yOffset, Point fromCell) {
        return this.applyPreset(preset, cell, xMirror, yMirror, xOffset, yOffset, fromCell, true);
    }

    public boolean applyPreset(ModularPreset preset, Point cell, boolean xMirror, boolean yMirror, int xOffset, int yOffset, Point fromCell, boolean addUsage) {
        this.debug("Attempting to apply preset " + preset.getClass().getSimpleName() + " to (" + cell.x + ", " + cell.y + ")");
        int placeTileX = this.getCellRealX(cell.x) + xOffset;
        int placeTileY = this.getCellRealY(cell.y) + yOffset;
        if (!preset.canPlace(this.level, placeTileX, placeTileY)) {
            this.debug("Preset would not allow to be there");
            return false;
        }
        if (this.isCellsOutOfMap(preset, cell)) {
            this.debug("Preset out of map");
            return false;
        }
        if (this.isCellsOccupied(preset, cell)) {
            this.debug("Cells occupied");
            return false;
        }
        if (!this.canApplyPreset(preset, cell, xMirror, yMirror, fromCell)) {
            this.debug("Level did not allow preset to be placed");
            return false;
        }
        this.debug("Applied preset " + preset.getClass().getSimpleName() + " to (" + cell.x + ", " + cell.y + ")");
        Preset toPlacePreset = preset;
        if (xMirror) {
            try {
                toPlacePreset = toPlacePreset.mirrorX();
            }
            catch (PresetMirrorException e) {
                xMirror = false;
            }
        }
        if (yMirror) {
            try {
                toPlacePreset = toPlacePreset.mirrorY();
            }
            catch (PresetMirrorException e) {
                yMirror = false;
            }
        }
        Preset replacedPreset = Preset.copyFromLevel(this.level, placeTileX, placeTileY, preset.width, preset.height);
        toPlacePreset.applyToLevel(this.level, placeTileX, placeTileY);
        for (int i = 0; i < preset.sectionWidth; ++i) {
            for (int j = 0; j < preset.sectionHeight; ++j) {
                Point p = new Point(cell.x + i, cell.y + j);
                Preset replaced = replacedPreset.subPreset(i * preset.sectionRes, j * preset.sectionRes, preset.sectionRes, preset.sectionRes);
                PlacedPreset pPreset = new PlacedPreset(this, p, preset, i, j, xMirror, yMirror, fromCell == null ? null : this.getPlacedPreset(fromCell), replaced);
                this.placedPresets.put(p, pPreset);
                this.openCells.add(p);
                if (!this.allowRoundTrips) continue;
                this.openAround(pPreset, xOffset, yOffset);
            }
        }
        if (addUsage) {
            if (!this.presetUsage.containsKey(preset)) {
                this.presetUsage.put(preset, 1);
            } else {
                this.presetUsage.put(preset, this.presetUsage.get(preset) + 1);
            }
        }
        return true;
    }

    private boolean applyFillPreset(ModularPreset preset, Point cell, boolean xMirror, boolean yMirror, int xOffset, int yOffset) {
        this.debug("Attempting to apply fill preset " + preset.getClass().getSimpleName() + " to (" + cell.x + ", " + cell.y + ")");
        int placeTileX = this.getCellRealX(cell.x) + xOffset;
        int placeTileY = this.getCellRealY(cell.y) + yOffset;
        if (!preset.canPlace(this.level, placeTileX, placeTileY)) {
            this.debug("Preset would not allow to be there");
            return false;
        }
        if (this.isCellsOutOfMap(preset, cell)) {
            this.debug("Preset out of map");
            return false;
        }
        if (this.isCellsOccupied(preset, cell)) {
            this.debug("Cells occupied");
            return false;
        }
        this.debug("Applied preset " + preset.getClass().getSimpleName() + " to (" + cell.x + ", " + cell.y + ")");
        Preset toPlacePreset = preset;
        if (xMirror) {
            try {
                toPlacePreset = toPlacePreset.mirrorX();
            }
            catch (PresetMirrorException e) {
                xMirror = false;
            }
        }
        if (yMirror) {
            try {
                toPlacePreset = toPlacePreset.mirrorY();
            }
            catch (PresetMirrorException e) {
                yMirror = false;
            }
        }
        Preset replacedPreset = Preset.copyFromLevel(this.level, placeTileX, placeTileY, preset.width, preset.height);
        toPlacePreset.applyToLevel(this.level, placeTileX, placeTileY);
        for (int i = 0; i < preset.sectionWidth; ++i) {
            for (int j = 0; j < preset.sectionHeight; ++j) {
                Point p = new Point(cell.x + i, cell.y + j);
                Preset replaced = replacedPreset.subPreset(i * preset.sectionRes, j * preset.sectionRes, preset.sectionRes, preset.sectionRes);
                this.placedPresets.put(p, new PlacedPreset(this, p, preset, i, j, xMirror, yMirror, null, replaced));
            }
        }
        return true;
    }

    public boolean replacePreset(Point cell, ModularPreset newPreset, boolean xMirror, boolean yMirror, int xOffset, int yOffset) {
        PlacedPreset oldPreset = this.getPlacedPreset(cell);
        if (oldPreset == null) {
            this.debug("Could not find old preset to replace");
            return false;
        }
        if (oldPreset.preset.sectionWidth != newPreset.sectionWidth || oldPreset.preset.sectionHeight != newPreset.sectionHeight) {
            this.debug("Replace preset are not the same dimensions as old one");
            return false;
        }
        if (oldPreset.realX != 0) {
            cell.x -= oldPreset.realX;
        }
        if (oldPreset.realY != 0) {
            cell.y -= oldPreset.realY;
        }
        Preset toPlacePreset = newPreset;
        if (xMirror) {
            try {
                toPlacePreset = toPlacePreset.mirrorX();
            }
            catch (PresetMirrorException e) {
                xMirror = false;
            }
        }
        if (yMirror) {
            try {
                toPlacePreset = toPlacePreset.mirrorY();
            }
            catch (PresetMirrorException e) {
                yMirror = false;
            }
        }
        this.debug("Replaced preset " + newPreset.getClass().getSimpleName() + " to (" + cell.x + ", " + cell.y + ")");
        int placeTileX = this.getCellRealX(cell.x) + xOffset;
        int placeTileY = this.getCellRealY(cell.y) + yOffset;
        toPlacePreset.applyToLevel(this.level, placeTileX, placeTileY);
        for (int i = 0; i < newPreset.sectionWidth; ++i) {
            for (int j = 0; j < newPreset.sectionHeight; ++j) {
                Point p = new Point(cell.x + i, cell.y + j);
                PlacedPreset old = this.getPlacedPreset(p);
                PlacedPreset n = new PlacedPreset(this, p, newPreset, i, j, xMirror, yMirror, old.from, old.replacedLevel);
                this.placedPresets.put(p, n);
                n.openDirs(this.level, xOffset, yOffset, this.random);
                if (!this.allowRoundTrips) continue;
                this.openAround(n, xOffset, yOffset);
            }
        }
        return true;
    }

    private void openAround(PlacedPreset preset, int xOffset, int yOffset) {
        for (int i = 0; i < 4; ++i) {
            PlacedPreset other;
            if (!preset.isPresetOpenDir(i) || (other = this.getPlacedPreset(this.getNextCell(preset.cell, i))) == null || !other.isPresetOpenDir(i + 2)) continue;
            this.debug("Opened from " + preset.cell.x + "," + preset.cell.y + " to " + other.cell.x + "," + other.cell.y);
            preset.openDirs(this.level, other, xOffset, yOffset, this.random);
        }
    }

    public void fixHeuristic() {
        for (PlacedPreset p : this.getPlacedPresets()) {
            if (p.from == null || p.heuristic <= p.from.heuristic + 1) continue;
            p.fixHeuristic(p.from.heuristic + 1);
            this.fixHeuristic();
        }
    }

    private boolean isCellsOccupied(ModularPreset preset, Point placeCell) {
        for (int i = 0; i < preset.sectionWidth; ++i) {
            for (int j = 0; j < preset.sectionHeight; ++j) {
                if (!this.placedPresets.containsKey(new Point(placeCell.x + i, placeCell.y + j))) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isCellsOutOfMap(ModularPreset preset, Point placeCell) {
        return placeCell.x < 0 || placeCell.x + preset.sectionWidth > this.cellsWidth || placeCell.y < 0 || placeCell.y + preset.sectionHeight > this.cellsHeight;
    }

    public boolean canApplyPreset(ModularPreset preset, Point cell, boolean xMirror, boolean yMirror, Point fromCell) {
        return true;
    }

    public static Point getStartCell(Level level, int cellsWidth, int cellsHeight) {
        GameRandom r = new GameRandom(level.getSeed());
        int xOffset = GameMath.limit(cellsWidth - 2, 0, 4);
        int yOffset = GameMath.limit(cellsHeight - 2, 0, 4);
        int x = r.nextInt(cellsWidth - xOffset) + xOffset / 2;
        int y = r.nextInt(cellsHeight - yOffset) + yOffset / 2;
        return new Point(x, y);
    }

    public Point getStartCell() {
        return ModularGeneration.getStartCell(this.level, this.cellsWidth - this.startPreset.sectionWidth, this.cellsHeight - this.startPreset.sectionHeight);
    }

    public static int getCellRealPos(int cellPos, int cellRes) {
        return cellPos * cellRes;
    }

    public int getCellRealX(int x) {
        return ModularGeneration.getCellRealPos(x, this.cellRes);
    }

    public int getCellRealY(int y) {
        return ModularGeneration.getCellRealPos(y, this.cellRes);
    }

    private boolean isCellOpen(Point cell, int dir) {
        dir %= 4;
        if (this.placedPresets.containsKey(cell)) {
            return this.placedPresets.get(cell).isPresetOpenDir(dir);
        }
        this.debug("Did not find placedPreset at " + cell.toString());
        return false;
    }

    public Point getNextCell(Point cell, int dir) {
        if ((dir %= 4) == 0) {
            return new Point(cell.x, cell.y - 1);
        }
        if (dir == 1) {
            return new Point(cell.x + 1, cell.y);
        }
        if (dir == 2) {
            return new Point(cell.x, cell.y + 1);
        }
        if (dir == 3) {
            return new Point(cell.x - 1, cell.y);
        }
        return new Point(cell.x, cell.y);
    }

    public void addRandomMobs(int xOffset, int yOffset, String[] mobStringIDs, int iterations, int maxAmount) {
        this.addRandomMobs(xOffset, yOffset, mobStringIDs, iterations, maxAmount, true);
    }

    public void addRandomMobs(int xOffset, int yOffset, String[] mobStringIDs, int iterations, int maxAmount, boolean searchOnlyPlaced) {
        Object[] cellList = null;
        if (searchOnlyPlaced) {
            cellList = this.placedPresets.keySet().toArray();
        }
        int mobsSpawned = 0;
        for (int i = 0; i < iterations && mobsSpawned <= maxAmount; ++i) {
            Point cell;
            if (searchOnlyPlaced) {
                cell = (Point)cellList[this.random.nextInt(cellList.length)];
            } else {
                int randomCellX = this.random.nextInt(this.cellsWidth);
                int randomCellY = this.random.nextInt(this.cellsHeight);
                cell = new Point(randomCellX, randomCellY);
            }
            if (!searchOnlyPlaced && !this.placedPresets.containsKey(cell)) continue;
            int randomTileX = xOffset + this.getCellRealX(cell.x) + this.random.nextInt(this.cellRes);
            int randomTileY = yOffset + this.getCellRealY(cell.y) + this.random.nextInt(this.cellRes);
            String randomStringID = mobStringIDs[this.random.nextInt(mobStringIDs.length)];
            Mob mob = MobRegistry.getMob(randomStringID, this.level);
            mob.resetUniqueID(this.random);
            mob.setPos(randomTileX * 32 + 16, randomTileY * 32 + 16, true);
            if (mob.collidesWith(this.level)) continue;
            this.level.entityManager.mobs.add(mob);
            ++mobsSpawned;
        }
    }

    private boolean printDebug() {
        return this.printDebug;
    }

    private void debug(String s) {
        if (this.printDebug()) {
            int inline = Thread.currentThread().getStackTrace().length - this.traceStart;
            for (int i = 0; i < inline; ++i) {
                s = "\t" + s;
            }
            System.out.println(s);
        }
    }

    public PlacedPreset getPlacedPreset(Point cell) {
        return this.placedPresets.get(cell);
    }

    public Collection<PlacedPreset> getPlacedPresets() {
        return this.placedPresets.values();
    }
}

