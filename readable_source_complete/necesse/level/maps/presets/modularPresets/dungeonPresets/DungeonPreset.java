/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.dungeonPresets;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetCopyException;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class DungeonPreset
extends ModularPreset {
    public final int wall = ObjectRegistry.getObjectID("dungeonwall");
    public final int floor = TileRegistry.getTileID("dungeonfloor");
    public final int dungeonChest = ObjectRegistry.getObjectID("dungeonchest");
    public final int door = ObjectRegistry.getObjectID("dungeondoor");
    public final int bookshelf = ObjectRegistry.getObjectID("dungeonbookshelf");
    public final GlyphTrapCustomApply glyphTrapCustomApply;

    private DungeonPreset(int sectionWidth, int sectionHeight, GameRandom random, boolean setup) {
        super(sectionWidth, sectionHeight, 15, 3, 2);
        this.openObject = 0;
        this.openTile = this.floor;
        this.closeObject = this.wall;
        this.closeTile = this.floor;
        if (setup) {
            this.fillTile(0, 0, this.width, this.height, this.floor);
            this.fillObject(0, 0, this.width, this.height, 0);
            this.boxObject(0, 0, this.width, this.height, this.wall);
            this.boxObject(1, 1, this.width - 2, this.height - 2, this.wall);
            this.onObjectApply((level, layerID, levelX, levelY, object, objectRotation, blackboard) -> {
                if (object == this.bookshelf) {
                    int chance = 8;
                    int maxBooks = 100;
                    ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                    if (objEnt != null && objEnt.implementsOEInventory()) {
                        Inventory inv = ((OEInventory)((Object)objEnt)).getInventory();
                        for (int i = 0; i < inv.getSize(); ++i) {
                            int bookCount = this.getBookCount(random, 0, chance, maxBooks);
                            if (bookCount <= 0) continue;
                            inv.setItem(i, new InventoryItem("book", bookCount));
                        }
                    } else {
                        GameLog.warn.println("Could not add books to dungeon bookshelf at " + levelX + ", " + levelY);
                    }
                }
            });
        }
        this.glyphTrapCustomApply = new GlyphTrapCustomApply(0.25f, random);
        this.addCustomApply(this.glyphTrapCustomApply);
    }

    public DungeonPreset(int sectionWidth, int sectionHeight, GameRandom random) {
        this(sectionWidth, sectionHeight, random, true);
    }

    public DungeonPreset(int sectionWidth, int sectionHeight) {
        this(sectionWidth, sectionHeight, null);
    }

    @Override
    protected DungeonPreset newModularObject(int sectionWidth, int sectionHeight, int sectionRes, int openingSize, int openingDepth) {
        return new DungeonPreset(sectionWidth, sectionHeight, null, false);
    }

    @Override
    public void openLevel(Level level, int x, int y, int xOffset, int yOffset, int dir, GameRandom random, int cellRes) {
        block9: {
            int trapX;
            int wireID;
            boolean[] plates;
            int pressurePlate;
            int trap;
            int middle;
            boolean trapDir;
            int levelY;
            int levelX;
            block12: {
                int farEnd;
                block11: {
                    block10: {
                        super.openLevel(level, x, y, xOffset, yOffset, dir, random, cellRes);
                        if (!random.getEveryXthChance(10)) break block9;
                        levelX = x * cellRes + xOffset;
                        levelY = y * cellRes + yOffset;
                        farEnd = cellRes - this.openingDepth;
                        trapDir = random.nextBoolean();
                        middle = cellRes / 2 + (trapDir ? this.openingSize + 2 : -this.openingSize - 2) / 2;
                        trap = ObjectRegistry.getObjectID(random.getOneOf("dungeonflametrap", "dungeonarrowtrap", "dungeonvoidtrap"));
                        pressurePlate = ObjectRegistry.getObjectID("dungeonpressureplate");
                        plates = new boolean[this.openingSize];
                        int chance = 0;
                        int chanceInc = 2;
                        int indexOffset = random.nextInt(this.openingSize);
                        for (int i = 0; i < this.openingSize; ++i) {
                            int index = (i + indexOffset) % this.openingSize;
                            if (chance > 0 && random.nextInt(chance) != 0) continue;
                            plates[index] = true;
                            chance += chanceInc;
                        }
                        wireID = random.nextInt(4);
                        if (dir != 0) break block10;
                        trapX = levelX + middle;
                        int trapY = levelY + 1;
                        level.setObject(trapX, trapY, trap, trapDir ? 3 : 1);
                        level.wireManager.setWire(trapX, trapY, wireID, true);
                        for (int i = 0; i < this.openingSize; ++i) {
                            if (plates[i]) {
                                level.setObject(trapX + (trapDir ? -i - 1 : i + 1), trapY, pressurePlate);
                            }
                            level.wireManager.setWire(trapX + (trapDir ? -i - 1 : i + 1), trapY, wireID, true);
                        }
                        break block9;
                    }
                    if (dir != 1) break block11;
                    trapX = levelX + farEnd;
                    int trapY = levelY + middle;
                    level.setObject(trapX, trapY, trap, trapDir ? 0 : 2);
                    level.wireManager.setWire(trapX, trapY, wireID, true);
                    for (int i = 0; i < this.openingSize; ++i) {
                        if (plates[i]) {
                            level.setObject(trapX, trapY + (trapDir ? -i - 1 : i + 1), pressurePlate);
                        }
                        level.wireManager.setWire(trapX, trapY + (trapDir ? -i - 1 : i + 1), wireID, true);
                    }
                    break block9;
                }
                if (dir != 2) break block12;
                trapX = levelX + middle;
                int trapY = levelY + farEnd;
                level.setObject(trapX, trapY, trap, trapDir ? 3 : 1);
                level.wireManager.setWire(trapX, trapY, wireID, true);
                for (int i = 0; i < this.openingSize; ++i) {
                    if (plates[i]) {
                        level.setObject(trapX + (trapDir ? -i - 1 : i + 1), trapY, pressurePlate);
                    }
                    level.wireManager.setWire(trapX + (trapDir ? -i - 1 : i + 1), trapY, wireID, true);
                }
                break block9;
            }
            if (dir != 3) break block9;
            trapX = levelX + 1;
            int trapY = levelY + middle;
            level.setObject(trapX, trapY, trap, trapDir ? 0 : 2);
            level.wireManager.setWire(trapX, trapY, wireID, true);
            for (int i = 0; i < this.openingSize; ++i) {
                if (plates[i]) {
                    level.setObject(trapX, trapY + (trapDir ? -i - 1 : i + 1), pressurePlate);
                }
                level.wireManager.setWire(trapX, trapY + (trapDir ? -i - 1 : i + 1), wireID, true);
            }
        }
    }

    private int getBookCount(GameRandom random, int startCount, int chance, int max) {
        boolean add = random.getEveryXthChance(chance);
        if (startCount >= max) {
            return max;
        }
        if (add) {
            return this.getBookCount(random, startCount + 1, chance, max);
        }
        return startCount;
    }

    protected void indicatePlaceForRandomSpellTrap(int presetTileX, int presetTileY) {
        this.glyphTrapCustomApply.positions.add(new Point(presetTileX, presetTileY));
    }

    public static class GlyphTrapCustomApply
    implements Preset.CustomApply {
        public static GameRandom random;
        public final ArrayList<Point> positions = new ArrayList();
        public float chanceToSpawn;

        public GlyphTrapCustomApply(float chanceToSpawn, GameRandom random) {
            this.chanceToSpawn = chanceToSpawn;
            if (random != null) {
                GlyphTrapCustomApply.random = random;
            }
        }

        @Override
        public Preset.UndoLogic applyToLevel(Level level, int presetX, int presetY, GameBlackboard blackboard) {
            if (this.positions.isEmpty()) {
                return null;
            }
            if (random.getChance(this.chanceToSpawn)) {
                Point position = random.getOneOf(this.positions);
                int trapObject = ObjectRegistry.getObjectID(random.getOneOf("glyphtrapchicken", "glyphtrapreversedamage", "glyphtrapbounce"));
                level.setObject(presetX + position.x, presetY + position.y, trapObject);
            }
            return null;
        }

        @Override
        public Preset.CustomApply copy(int xOffset, int yOffset, int maxWidth, int maxHeight) throws PresetCopyException {
            GlyphTrapCustomApply copy = new GlyphTrapCustomApply(this.chanceToSpawn, random);
            for (Point position : this.positions) {
                copy.positions.add(new Point(position.x + xOffset, position.y + yOffset));
            }
            return copy;
        }

        @Override
        public Preset.CustomApply mirrorX(int width) throws PresetMirrorException {
            GlyphTrapCustomApply copy = new GlyphTrapCustomApply(this.chanceToSpawn, random);
            for (Point position : this.positions) {
                copy.positions.add(new Point(PresetUtils.getMirroredValue(position.x, width), position.y));
            }
            return copy;
        }

        @Override
        public Preset.CustomApply mirrorY(int height) throws PresetMirrorException {
            GlyphTrapCustomApply copy = new GlyphTrapCustomApply(this.chanceToSpawn, random);
            for (Point position : this.positions) {
                copy.positions.add(new Point(position.x, PresetUtils.getMirroredValue(position.y, height)));
            }
            return copy;
        }

        @Override
        public Preset.CustomApply rotate(PresetRotation angle, int width, int height) throws PresetRotateException {
            GlyphTrapCustomApply copy = new GlyphTrapCustomApply(this.chanceToSpawn, random);
            for (Point position : this.positions) {
                copy.positions.add(PresetUtils.getRotatedPointInSpace(position.x, position.y, width, height, angle));
            }
            return copy;
        }
    }
}

