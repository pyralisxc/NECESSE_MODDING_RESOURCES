/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.BiomeCenterWorldPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.furniturePresets.BedDresserPreset;
import necesse.level.maps.presets.furniturePresets.BenchPreset;
import necesse.level.maps.presets.furniturePresets.BookshelfClockPreset;
import necesse.level.maps.presets.furniturePresets.BookshelvesPreset;
import necesse.level.maps.presets.furniturePresets.CabinetsPreset;
import necesse.level.maps.presets.furniturePresets.DeskBookshelfPreset;
import necesse.level.maps.presets.furniturePresets.DinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.DisplayStandClockPreset;
import necesse.level.maps.presets.furniturePresets.ModularDinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.ModularTablesPreset;
import necesse.level.maps.presets.furniturePresets.SingleChestPreset;
import necesse.level.maps.presets.set.FurnitureSet;

public class DesertTempleWorldPreset
extends BiomeCenterWorldPreset {
    public Dimension size = new Dimension(40, 40);

    public DesertTempleWorldPreset() {
        super(BiomeRegistry.DESERT);
        this.randomAttempts = 50;
        this.sectionMaxRegionCount = 625;
        this.sectionMinRegionCount = 36;
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
    }

    @Override
    public boolean isValidSectionRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        return true;
    }

    @Override
    public boolean isValidFinalRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        int startTileX = centerTileX - this.size.width / 2;
        int startTileY = centerTileY - this.size.height / 2;
        return this.runCornerCheck(startTileX, startTileY, this.size.width, this.size.height, (tileX, tileY) -> generatorStack.getLazyBiomeID(tileX, tileY) == this.biome.getID());
    }

    @Override
    public void onFoundRegion(int regionX, int regionY, GameRandom random, final LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        final int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        final int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        final float centerRange = 15.5f;
        LinesGeneration lg = new LinesGeneration(centerTileX, centerTileY, centerRange);
        BiPredicate<LinesGeneration, Integer> isValidArm = new BiPredicate<LinesGeneration, Integer>(){

            @Override
            public boolean test(LinesGeneration arm, Integer padding) {
                return DesertTempleWorldPreset.this.isTileWithinBounds(arm.x2, arm.y2, presetsRegion, padding) && generatorStack.getLazyBiomeID(arm.x2, arm.y2) == DesertTempleWorldPreset.this.biome.getID();
            }
        };
        int armAngle = random.nextInt(360);
        int arms = 8;
        int anglePerArm = 360 / arms;
        for (int i = 0; i < arms; ++i) {
            LinesGeneration lastArm = lg.addMultiArm(random, armAngle += anglePerArm, 15, random.getIntBetween(150, 200), 5.0f, 10.0f, 8.0f, 9.0f, armLG -> !isValidArm.test((LinesGeneration)armLG, 15));
            if (isValidArm.test(lastArm, 10)) continue;
            lg.removeLastLine();
        }
        if (!lg.getRoot().isEmpty()) {
            CellAutomaton ca = Performance.record(performanceTimer, "doCellularAutomaton", () -> lg.doCellularAutomaton(random));
            Performance.record(performanceTimer, "cleanHardEdges", ca::cleanHardEdges);
            int x = centerTileX - (int)Math.floor(centerRange);
            while ((double)x <= (double)centerTileX + Math.ceil(centerRange)) {
                int y = centerTileY - (int)Math.floor(centerRange);
                while ((double)y <= (double)centerTileY + Math.ceil(centerRange)) {
                    if (GameMath.getExactDistance(centerTileX, centerTileY, x, y) <= centerRange) {
                        ca.setAlive(x, y);
                    }
                    ++y;
                }
                ++x;
            }
            RegionTileWorldPresetGenerator tileGenerator = new RegionTileWorldPresetGenerator();
            final int sandBrickID = TileRegistry.getTileID("sandbrick");
            final int woodFloorID = TileRegistry.getTileID("woodfloor");
            Performance.record(performanceTimer, "addTileGenerator", () -> {
                ca.streamAliveOrdered().forEach(tile -> tileGenerator.addTile(tile.x, tile.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                    @Override
                    public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                        Performance.record(timer, "setTile", () -> {
                            if (random.getChance(0.75f)) {
                                level.setTile(tileX, tileY, sandBrickID);
                            } else {
                                level.setTile(tileX, tileY, woodFloorID);
                            }
                            level.setObject(tileX, tileY, 0);
                        });
                    }
                }));
                ca.placeEdgeWalls(tileGenerator, ObjectRegistry.getObjectID("deepsandstonewall"), true);
                final GameObject[] breakObjects = new GameObject[]{ObjectRegistry.getObject("crate"), ObjectRegistry.getObject("vase")};
                ca.streamAliveOrdered().forEach(tile -> tileGenerator.addTile(tile.x, tile.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                    @Override
                    public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                        Performance.record(timer, "setBreakObject", () -> {
                            GameObject breakObject;
                            if (random.getChance(0.02f) && (breakObject = random.getOneOf(breakObjects)).canPlace(level, tileX, tileY, 0, false) == null) {
                                breakObject.placeObject(level, tileX, tileY, 0, false);
                            }
                        });
                    }
                }));
                int x = centerTileX - (int)Math.floor(centerRange);
                while ((double)x <= (double)centerTileX + Math.ceil(centerRange)) {
                    int y = centerTileY - (int)Math.floor(centerRange);
                    while ((double)y <= (double)centerTileY + Math.ceil(centerRange)) {
                        if (GameMath.getExactDistance(centerTileX, centerTileY, x, y) <= centerRange) {
                            tileGenerator.addTile(x, y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                                @Override
                                public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                                    GameObject breakObject;
                                    if (random.getChance(0.05f) && (breakObject = random.getOneOf(breakObjects)).canPlace(level, tileX, tileY, 0, false) == null) {
                                        breakObject.placeObject(level, tileX, tileY, 0, false);
                                    }
                                }
                            });
                        }
                        ++y;
                    }
                    ++x;
                }
                LootTable templeChestLootTable = new LootTable();
                for (int i = 0; i < 5; ++i) {
                    templeChestLootTable.items.add(LootTablePresets.desertDeepCrate);
                }
                TicketSystemList<Function<GameRandom, Preset>> templeFurniture = new TicketSystemList<Function<GameRandom, Preset>>();
                templeFurniture.addObject(100, r -> new BedDresserPreset(FurnitureSet.palm, 2));
                templeFurniture.addObject(100, r -> new BenchPreset(FurnitureSet.palm, 2));
                templeFurniture.addObject(100, r -> new BookshelfClockPreset(FurnitureSet.palm, 2));
                templeFurniture.addObject(100, r -> new BookshelvesPreset(FurnitureSet.palm, 2, 3));
                templeFurniture.addObject(100, r -> new CabinetsPreset(FurnitureSet.palm, 2, 3));
                templeFurniture.addObject(100, r -> new DeskBookshelfPreset(FurnitureSet.palm, 2));
                templeFurniture.addObject(100, r -> new DinnerTablePreset(FurnitureSet.palm, 2));
                templeFurniture.addObject(100, r -> new DisplayStandClockPreset(FurnitureSet.palm, 2, (GameRandom)r, null, new Object[0]));
                templeFurniture.addObject(100, r -> new ModularDinnerTablePreset(FurnitureSet.palm, 2, 1));
                templeFurniture.addObject(100, r -> new ModularTablesPreset(FurnitureSet.palm, 2, 2, true));
                templeFurniture.addObject(100, r -> new SingleChestPreset(FurnitureSet.palm, 2, (GameRandom)r, templeChestLootTable, new Object[0]));
                ca.placeFurnitureGettersPresets(tileGenerator, templeFurniture, 0.2f);
            });
            tileGenerator.forEachRegion(new RegionTileWorldPresetGenerator.ForEachFunction(){

                @Override
                public void handle(int regionX, int regionY, LevelPresetsRegion.WorldPresetPlaceFunction placeFunction) {
                    int tileX = GameMath.getTileCoordByRegion(regionX);
                    int tileY = GameMath.getTileCoordByRegion(regionY);
                    presetsRegion.addPreset((WorldPreset)DesertTempleWorldPreset.this, tileX, tileY, new Dimension(16, 16), new String[]{"minibiomes", "loot"}, placeFunction);
                }
            });
        }
        int startTileX = centerTileX - this.size.width / 2;
        int startTileY = centerTileY - this.size.height / 2;
        presetsRegion.addPreset((WorldPreset)this, startTileX, startTileY, this.size, new String[]{"minibiomes", "loot"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

            @Override
            public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                int columnsCount = random.getIntBetween(6, 8);
                int anglePerColumn = 360 / columnsCount;
                int columnAngle = random.nextInt(360);
                float columnDistance = centerRange - centerRange / 3.0f;
                int columnID = ObjectRegistry.getObjectID("deepsandstonecolumn");
                for (int i = 0; i < columnsCount; ++i) {
                    Point2D.Float columnDir = GameMath.getAngleDir(columnAngle += random.getIntOffset(anglePerColumn, anglePerColumn / 5));
                    level.setObject(centerTileX + (int)(columnDir.x * columnDistance), centerTileY + (int)(columnDir.y * columnDistance), columnID);
                }
                level.setObject(centerTileX, centerTileY, ObjectRegistry.getObjectID("templepedestal"));
            }
        });
    }
}

