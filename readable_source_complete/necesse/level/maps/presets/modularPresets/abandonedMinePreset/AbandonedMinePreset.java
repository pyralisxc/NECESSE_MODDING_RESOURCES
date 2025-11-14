/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.SkeletonMinerMob;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.presets.modularPresets.ModularPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineBedroomPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineBedroomRPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineBlacksmithPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineBlacksmithRPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineCrateRoomPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineDiningPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineDiningRPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineHallwayPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineLibraryPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineLibraryRPreset;

public class AbandonedMinePreset
extends ModularPreset {
    public final int wall;
    public final int floor;
    public final int storagebox;
    public final int door;
    public final int doorOpen;
    public GameRandom random;

    public AbandonedMinePreset(int sectionWidth, int sectionHeight, int openingSize, int openingDepth, GameRandom random) {
        super(sectionWidth, sectionHeight, 7, openingSize, openingDepth);
        this.random = random;
        this.overlap = true;
        this.wall = ObjectRegistry.getObjectID("deepstonewall");
        this.floor = TileRegistry.deepStoneFloorID;
        this.storagebox = ObjectRegistry.getObjectID("storagebox");
        this.door = ObjectRegistry.getObjectID("deepstonedoor");
        this.doorOpen = ObjectRegistry.getObjectID("deepstonedooropen");
    }

    public AbandonedMinePreset(int sectionWidth, int sectionHeight, GameRandom random) {
        this(sectionWidth, sectionHeight, 5, 1, random);
    }

    public AbandonedMinePreset(int sectionWidth, int sectionHeight) {
        this(sectionWidth, sectionHeight, null);
    }

    @Override
    protected AbandonedMinePreset newModularObject(int sectionWidth, int sectionHeight, int sectionRes, int openingSize, int openingDepth) {
        return new AbandonedMinePreset(sectionWidth, sectionHeight, openingSize, openingDepth, this.random);
    }

    protected void fillOpeningRealSuper(Level level, int x, int y, int dir, int object, int tile) {
        super.fillOpeningReal(level, x, y, dir, object, tile);
    }

    @Override
    public void fillOpeningReal(Level level, int x, int y, int dir, int object, int tile) {
        if (dir == 0) {
            level.setObject(x + this.openingSize / 2, y, this.random.nextBoolean() ? this.door : this.doorOpen, dir);
        } else if (dir == 1) {
            level.setObject(x, y + this.openingSize / 2, this.random.nextBoolean() ? this.door : this.doorOpen, dir);
        } else if (dir == 2) {
            level.setObject(x + this.openingSize / 2, y, this.random.nextBoolean() ? this.door : this.doorOpen, dir);
        } else if (dir == 3) {
            level.setObject(x, y + this.openingSize / 2, this.random.nextBoolean() ? this.door : this.doorOpen, dir);
        }
    }

    public void addSkeletonMiner(int tileX, int tileY, GameRandom random, float chance) {
        this.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (random.getChance(chance)) {
                SkeletonMinerMob mob = new SkeletonMinerMob();
                level.entityManager.addMob(mob, levelX * 32 + 16, levelY * 32 + 16);
                mob.canDespawn = false;
                return (level1, presetX, presetY) -> mob.remove();
            }
            return null;
        });
    }

    public static Rectangle generateAbandonedMineOnLevel(Level level, GameRandom random, List<Rectangle> collisions) {
        ModularGeneration mg = new ModularGeneration(level, random.getIntBetween(6, 10), random.getIntBetween(6, 10), 6, 5, 1){

            @Override
            public Point getStartCell() {
                return new Point(this.cellsWidth / 2, this.cellsHeight / 2);
            }
        };
        int xOffset = 10 + random.nextInt(level.tileWidth - mg.cellRes * mg.cellsWidth - 20);
        int yOffset = 10 + random.nextInt(level.tileHeight - mg.cellRes * mg.cellsHeight - 20);
        Rectangle collision = new Rectangle(xOffset, yOffset, mg.cellRes * mg.cellsWidth, mg.cellRes * mg.cellsHeight);
        if (collisions != null && collisions.stream().anyMatch(c -> c.intersects(collision))) {
            return null;
        }
        AbandonedMineHallwayPreset path = new AbandonedMineHallwayPreset(mg.random, true, true, true, true);
        mg.setStartPreset(path);
        mg.initGeneration(xOffset, yOffset);
        Point startCell = mg.getStartCell();
        float hallwayChance = 0.1f;
        ArrayList<HallwayCell> openHallwayCells = new ArrayList<HallwayCell>();
        ArrayList<HallwayCell> closedHallwayCells = new ArrayList<HallwayCell>();
        for (int i = 0; i < 4; ++i) {
            openHallwayCells.add(new HallwayCell(new Point(startCell), i));
        }
        int hallwayCounter = 0;
        while (!openHallwayCells.isEmpty()) {
            HallwayCell current = (HallwayCell)openHallwayCells.remove(mg.random.nextInt(openHallwayCells.size()));
            Point prevCell = current.cell;
            while (true) {
                int cellOffset;
                Point nextCell = mg.getNextCell(prevCell, current.dir);
                Point oneOverCell = mg.getNextCell(nextCell, current.dir);
                if (closedHallwayCells.stream().anyMatch(c -> c.cell.equals(oneOverCell)) || closedHallwayCells.stream().anyMatch(c -> c.cell.equals(nextCell)) || nextCell.x < 0 || nextCell.x >= mg.cellsWidth || nextCell.y < 0 || nextCell.y >= mg.cellsHeight) break;
                if (current.dir == 0 || current.dir == 2) {
                    cellOffset = Math.abs(nextCell.y - startCell.y);
                    if (cellOffset % 2 == 0) {
                        if (mg.random.getChance(hallwayChance)) {
                            openHallwayCells.add(new HallwayCell(nextCell, 1));
                        }
                        if (mg.random.getChance(hallwayChance)) {
                            openHallwayCells.add(new HallwayCell(nextCell, 3));
                        }
                    }
                } else {
                    cellOffset = Math.abs(nextCell.x - startCell.x);
                    if (cellOffset % 2 == 0) {
                        if (mg.random.getChance(hallwayChance)) {
                            openHallwayCells.add(new HallwayCell(nextCell, 0));
                        }
                        if (mg.random.getChance(hallwayChance)) {
                            openHallwayCells.add(new HallwayCell(nextCell, 2));
                        }
                    }
                }
                mg.applyPreset(path, nextCell, false, false, xOffset, yOffset, prevCell);
                ++hallwayCounter;
                prevCell = nextCell;
            }
            closedHallwayCells.add(current);
        }
        for (int floorID : new int[]{TileRegistry.deepStoneBrickFloorID}) {
            mg.addPreset(new AbandonedMineBedroomPreset(mg.random, floorID), 100);
            mg.addPreset(new AbandonedMineBedroomRPreset(mg.random, floorID), 100);
            mg.addPreset(new AbandonedMineBlacksmithPreset(mg.random, floorID), 100);
            mg.addPreset(new AbandonedMineBlacksmithRPreset(mg.random, floorID), 100);
            mg.addPreset(new AbandonedMineCrateRoomPreset(mg.random, floorID), 150);
            mg.addPreset(new AbandonedMineDiningPreset(mg.random, floorID), 100);
            mg.addPreset(new AbandonedMineDiningRPreset(mg.random, floorID), 100);
            mg.addPreset(new AbandonedMineLibraryPreset(mg.random, floorID), 100);
            mg.addPreset(new AbandonedMineLibraryRPreset(mg.random, floorID), 100);
        }
        mg.tickGeneration(xOffset, yOffset, (int)((float)hallwayCounter * 1.5f));
        Object object = mg.getPlacedPresets().iterator();
        while (object.hasNext()) {
            PlacedPreset placedPreset = (PlacedPreset)object.next();
            if (!(placedPreset.preset instanceof AbandonedMineHallwayPreset)) continue;
            AbandonedMinePreset.fixHallway(level, mg, xOffset, yOffset, placedPreset);
        }
        mg.endGeneration();
        return collision;
    }

    private static void fixHallway(Level level, ModularGeneration mg, int xOffset, int yOffset, PlacedPreset preset) {
        for (int i = 0; i < 4; ++i) {
            Point nextCell = mg.getNextCell(preset.cell, i);
            PlacedPreset placedPreset = mg.getPlacedPreset(nextCell);
            if (placedPreset != null) continue;
            preset.preset.closeLevel(level, 0, 0, mg.getCellRealX(preset.cell.x) + xOffset, mg.getCellRealY(preset.cell.y) + yOffset, i, mg.cellRes);
        }
    }

    private static class HallwayCell {
        public final Point cell;
        public final int dir;

        public HallwayCell(Point cell, int dir) {
            this.cell = cell;
            this.dir = dir;
        }
    }
}

