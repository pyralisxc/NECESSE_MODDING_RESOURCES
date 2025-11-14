/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import necesse.engine.AreaFinder;
import necesse.engine.Settings;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.util.voronoi.DelaunayTriangulator;
import necesse.engine.util.voronoi.TriangleLine;
import necesse.engine.world.WorldEntity;
import necesse.gfx.drawables.WallShadowVariables;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.presets.FurnitureHousePreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
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
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class GraveyardIncursionLevel
extends IncursionLevel {
    public static int GRAVEYARD_AMBIENT_LIGHT = 60;

    public GraveyardIncursionLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public GraveyardIncursionLevel(LevelIdentifier identifier, BiomeMissionIncursionData incursion, WorldEntity worldEntity, AltarData altarData) {
        super(identifier, 150, 150, incursion, worldEntity);
        this.baseBiome = BiomeRegistry.GRAVEYARD;
        this.generateLevel(incursion, altarData);
    }

    public void generateLevel(BiomeMissionIncursionData incursionData, AltarData altarData) {
        GameRandom random = new GameRandom(incursionData.getUniqueID());
        int cryptAshID = TileRegistry.getTileID("cryptash");
        for (int tileX2 = 0; tileX2 < this.tileWidth; ++tileX2) {
            for (int tileY2 = 0; tileY2 < this.tileHeight; ++tileY2) {
                this.setTile(tileX2, tileY2, cryptAshID);
            }
        }
        TicketSystemList objects = new TicketSystemList();
        GameObject air = ObjectRegistry.getObject("air");
        objects.addObject(2000, air);
        objects.addObject(100, ObjectRegistry.getObject("cryptcoffin"));
        objects.addObject(100, ObjectRegistry.getObject("cryptcolumn"));
        objects.addObject(100, ObjectRegistry.getObject("cryptgravestone1"));
        objects.addObject(100, ObjectRegistry.getObject("cryptgravestone2"));
        objects.addObject(100, ObjectRegistry.getObject("deadwoodtree"));
        objects.addObject(100, ObjectRegistry.getObject("vase"));
        objects.addObject(30, ObjectRegistry.getObject("deadwoodcandles"));
        objects.addObject(5, ObjectRegistry.getObject("deadwoodbench"));
        objects.addObject(5, ObjectRegistry.getObject("deadwoodchair"));
        for (int tileX3 = 0; tileX3 < this.tileWidth; ++tileX3) {
            for (int tileY3 = 0; tileY3 < this.tileHeight; ++tileY3) {
                MultiTile multiTile;
                int rotation;
                GameObject object2 = (GameObject)objects.getRandomObject(random);
                if (object2.getID() == air.getID() || object2.canPlace(this, tileX3, tileY3, rotation = random.nextInt(4), false) != null || (multiTile = object2.getMultiTile(rotation)).streamOtherIDs(tileX3, tileY3).anyMatch(c -> this.getObjectID(c.tileX, c.tileY) != 0) || multiTile.getAdjacentTiles(tileX3, tileY3, true).stream().anyMatch(adjTile -> this.getObjectID(adjTile.x, adjTile.y) != 0)) continue;
                object2.placeObject(this, tileX3, tileY3, rotation, false);
            }
        }
        GenerationTools.fillMap(this, random, cryptAshID, -1, 0.0f, ObjectRegistry.getObjectID("cryptgrass"), -1, 0.2f, false, true);
        ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>();
        int edgePadding = -20;
        int usableWidth = this.tileWidth - edgePadding * 2;
        int usableHeight = this.tileHeight - edgePadding * 2;
        int resolution = 30;
        int resWidth = usableWidth / resolution;
        int resHeight = usableHeight / resolution;
        int xOffset = usableWidth % resolution / 2;
        int yOffset = usableHeight % resolution / 2;
        for (int x = 0; x < resWidth; ++x) {
            int minX = x * resolution + xOffset + edgePadding;
            int maxX = minX + resolution;
            for (int y = 0; y < resHeight; ++y) {
                int minY = y * resolution + yOffset + edgePadding;
                int maxY = minY + resolution;
                Point2D.Float point2 = new Point2D.Float(random.getIntBetween(minX, maxX), random.getIntBetween(minY, maxY));
                points.add(point2);
            }
        }
        ArrayList<TriangleLine> voronoiLines = new ArrayList<TriangleLine>();
        DelaunayTriangulator.compute(points, false, voronoiLines);
        int cryptPath = TileRegistry.getTileID("cryptpath");
        for (TriangleLine line : voronoiLines) {
            LinesGeneration.pathTiles(new Line2D.Float(line.p1, line.p2), true, (from, next) -> {
                for (int x = 0; x < 2; ++x) {
                    for (int y = 0; y < 2; ++y) {
                        int tileX = next.x + x;
                        int tileY = next.y + y;
                        if (!this.isTileWithinBounds(tileX, tileY)) continue;
                        this.setTile(tileX, tileY, cryptPath);
                        this.setObject(tileX, tileY, 0);
                    }
                }
            });
        }
        int fencesCount = Math.min(points.size(), random.getIntBetween(12, 18));
        int stoneFence = ObjectRegistry.getObjectID("cryptfence");
        int stoneFenceGate = ObjectRegistry.getObjectID("cryptfencegate");
        BiConsumer<Point, Integer> pointProgression = (point, dir) -> {
            switch (dir) {
                case 0: {
                    --point.y;
                    break;
                }
                case 1: {
                    ++point.x;
                    break;
                }
                case 2: {
                    ++point.y;
                    break;
                }
                default: {
                    --point.x;
                }
            }
        };
        for (int i = 0; i < fencesCount && !points.isEmpty(); ++i) {
            int pointsIndex = random.nextInt(points.size());
            Point2D.Float floatStartTile = points.remove(pointsIndex);
            Point startTile = new Point((int)floatStartTile.x, (int)floatStartTile.y);
            if (!this.isTileWithinBounds(startTile.x, startTile.y)) continue;
            ArrayList<OpenDirection> openDirections = new ArrayList<OpenDirection>();
            int startDirOffset = random.nextInt(4);
            for (int j = 0; j < 4; ++j) {
                if (j != 0 && !random.getEveryXthChance(2)) continue;
                openDirections.add(new OpenDirection(startTile.x, startTile.y, (j + startDirOffset) % 4, 0));
            }
            ArrayList fenceSections = new ArrayList();
            while (!openDirections.isEmpty()) {
                int currentLength;
                ArrayList<Point> fenceSection = new ArrayList<Point>();
                OpenDirection current = (OpenDirection)openDirections.remove(0);
                Point check = new Point(current.x, current.y);
                int maxLength = random.getIntBetween(8, 20);
                int checkAhead = random.nextInt(5);
                for (int j = 0; j < checkAhead; ++j) {
                    pointProgression.accept(check, current.dir);
                }
                for (currentLength = 0; !(currentLength >= maxLength || this.getTile((int)current.x, (int)current.y).isFloor || this.getTile((int)check.x, (int)check.y).isFloor || currentLength != 0 && (this.getObject((int)current.x, (int)current.y).isFence || this.getObject((int)check.x, (int)check.y).isFence)); ++currentLength) {
                    if (currentLength > 0 && currentLength < maxLength - 1) {
                        fenceSection.add(new Point(current));
                    }
                    this.setObject(current.x, current.y, stoneFence);
                    pointProgression.accept(current, current.dir);
                    pointProgression.accept(check, current.dir);
                }
                fenceSections.add(fenceSection);
                if (currentLength <= 3 || !random.getEveryXthChance(current.depth + 2)) continue;
                int dirOffset = random.nextInt(4);
                for (int j = 0; j < 4; ++j) {
                    int dir2 = (j + dirOffset) % 4;
                    if (dir2 == current.dir || !random.getEveryXthChance(1 + j)) continue;
                    openDirections.add(new OpenDirection(current.x, current.y, dir2, current.depth + 1));
                }
            }
            if (fenceSections.isEmpty()) continue;
            int gates = 1 + random.nextInt(fenceSections.size());
            for (int j = 0; j < gates && !fenceSections.isEmpty(); ++j) {
                int sectionIndex = random.nextInt(fenceSections.size());
                ArrayList section = (ArrayList)fenceSections.remove(sectionIndex);
                Point gateTile = (Point)random.getOneOf(section);
                if (gateTile == null) continue;
                this.setObject(gateTile.x, gateTile.y, stoneFenceGate);
            }
        }
        int housesCount = Math.min(points.size(), random.getIntBetween(12, 18));
        int deadwoodFloor = TileRegistry.getTileID("deadwoodfloor");
        TicketSystemList<Preset> furniture = new TicketSystemList<Preset>();
        furniture.addObject(100, (Object)new BedDresserPreset(FurnitureSet.deadwood, 2));
        furniture.addObject(100, (Object)new BenchPreset(FurnitureSet.deadwood, 2));
        furniture.addObject(100, (Object)new BookshelfClockPreset(FurnitureSet.deadwood, 2));
        furniture.addObject(100, (Object)new BookshelvesPreset(FurnitureSet.deadwood, 2, 3));
        furniture.addObject(100, (Object)new CabinetsPreset(FurnitureSet.deadwood, 2, 3));
        furniture.addObject(100, (Object)new DeskBookshelfPreset(FurnitureSet.deadwood, 2));
        furniture.addObject(100, (Object)new DinnerTablePreset(FurnitureSet.deadwood, 2));
        furniture.addObject(100, (Object)new DisplayStandClockPreset(FurnitureSet.deadwood, 2, null, null, new Object[0]));
        furniture.addObject(100, (Object)new ModularDinnerTablePreset(FurnitureSet.deadwood, 2, 1));
        furniture.addObject(50, (Object)new ModularTablesPreset(FurnitureSet.deadwood, 2, 2, true));
        for (int i = 0; i < housesCount && !points.isEmpty(); ++i) {
            int offsetY;
            int offsetX;
            int resolutionY;
            int resolutionX;
            int pointsIndex = random.nextInt(points.size());
            Point2D.Float centerTile = points.remove(pointsIndex);
            if (!this.isTileWithinBounds((int)centerTile.x, (int)centerTile.y)) continue;
            int houseWidth = random.getIntBetween(8, 14);
            int houseHeight = random.getIntBetween(7, 10);
            Preset preset = new FurnitureHousePreset(houseWidth, houseHeight, deadwoodFloor, WallSet.crypt, random, furniture, 0.8f);
            preset.addCanApplyRectEachPredicate(0, 0, houseWidth, houseHeight, 0, (level, levelX, levelY, dir) -> !level.getTile((int)levelX, (int)levelY).isFloor);
            AtomicReference doorTileRef = new AtomicReference();
            preset.onObjectApply((level, layerID, levelX, levelY, object, objectRotation, blackboard) -> {
                if (object == WallSet.crypt.doorClosed || object == WallSet.crypt.doorOpen) {
                    doorTileRef.set(new Point(levelX, levelY));
                }
            });
            int placeDir = random.nextInt(4);
            try {
                preset = preset.rotate(PresetRotation.toRotationAngle(placeDir));
            }
            catch (PresetRotateException gateTile) {
                // empty catch block
            }
            final FurnitureHousePreset finalPreset = preset;
            AreaFinder posFinder = new AreaFinder((int)centerTile.x, (int)centerTile.y, 5, true){

                @Override
                public boolean checkPoint(int x, int y) {
                    return finalPreset.canApplyToLevelCentered(GraveyardIncursionLevel.this, x, y);
                }
            };
            posFinder.runFinder();
            if (!posFinder.hasFound()) continue;
            Point tile = posFinder.getFirstFind();
            finalPreset.applyToLevelCentered(this, tile.x, tile.y);
            Point currentTile = (Point)doorTileRef.get();
            if (currentTile == null) continue;
            int angle = 0;
            switch (placeDir) {
                case 0: {
                    angle = random.getIntOffset(270, 45);
                    boolean doubleDoor = houseWidth % 2 == 0;
                    resolutionX = doubleDoor ? 2 : 3;
                    resolutionY = 2;
                    offsetX = -1;
                    offsetY = -1;
                    break;
                }
                case 1: {
                    angle = random.getIntOffset(0, 45);
                    boolean doubleDoor = houseHeight % 2 == 0;
                    resolutionX = 2;
                    resolutionY = doubleDoor ? 2 : 3;
                    offsetX = 1;
                    offsetY = -1;
                    break;
                }
                case 2: {
                    angle = random.getIntOffset(90, 45);
                    boolean doubleDoor = houseWidth % 2 == 0;
                    resolutionX = doubleDoor ? 2 : 3;
                    resolutionY = 2;
                    offsetX = -1;
                    offsetY = 1;
                    break;
                }
                case 3: {
                    angle = random.getIntOffset(180, 45);
                    boolean doubleDoor = houseHeight % 2 == 0;
                    resolutionX = 2;
                    resolutionY = doubleDoor ? 2 : 3;
                    offsetX = -1;
                    offsetY = -1;
                    break;
                }
                default: {
                    resolutionX = 0;
                    resolutionY = 0;
                    offsetX = 0;
                    offsetY = 0;
                }
            }
            Point2D.Float dir3 = GameMath.getAngleDir(angle);
            LinesGeneration.pathTilesBreak(new Line2D.Float(currentTile.x, currentTile.y, (float)currentTile.x + dir3.x * 30.0f, (float)currentTile.y + dir3.y * 30.0f), true, (from, next) -> {
                boolean found = false;
                for (int x = 0; x < resolutionX; ++x) {
                    for (int y = 0; y < resolutionY; ++y) {
                        int tileX = next.x + x + offsetX;
                        int tileY = next.y + y + offsetY;
                        if (!this.isTileWithinBounds(tileX, tileY) || this.getTile((int)tileX, (int)tileY).isFloor) continue;
                        this.setTile(tileX, tileY, cryptPath);
                        this.setObject(tileX, tileY, 0);
                        found = true;
                    }
                }
                return found;
            });
        }
        PresetGeneration presets = new PresetGeneration(this);
        int spawnPadding = 40;
        int spawnMidX = random.getIntOffset(this.tileWidth / 2, this.tileWidth / 2 - spawnPadding * 2);
        int spawnMidY = random.getIntOffset(this.tileHeight / 2, this.tileHeight / 2 - spawnPadding * 2);
        TriangleLine midPoint = voronoiLines.stream().min(Comparator.comparingDouble(t -> t.p1.distance(spawnMidX, spawnMidY))).orElse(null);
        if (midPoint != null) {
            IncursionBiome.generateEntrance(this, presets, random, (int)midPoint.p1.x + 1, (int)midPoint.p1.y + 1, 30, cryptAshID, "cryptpath", null, "cryptcolumn");
        } else {
            IncursionBiome.generateEntrance(this, presets, random, this.tileWidth / 2, this.tileHeight / 2, 30, cryptAshID, "cryptpath", null, "cryptcolumn");
        }
        this.generatePresetsBasedOnPerks(altarData, presets, random, this.baseBiome);
        if (incursionData instanceof BiomeExtractionIncursionData) {
            GameObject extractionObject = ObjectRegistry.getObject("cryptnightsteelorerocksmall");
            GenerationTools.generateGuaranteedRandomVeins(this, random, 25, 1, 1, (level, tileX, tileY) -> level.getTileID(tileX, tileY) == cryptAshID && extractionObject.canPlace(level, tileX, tileY, 0, false) == null, (level, tileX, tileY) -> extractionObject.placeObject(level, tileX, tileY, 0, false));
        }
        GameObject upgradeShardObject = ObjectRegistry.getObject("cryptupgradeshardorerocksmall");
        GenerationTools.generateGuaranteedRandomVeins(this, random, 23, 1, 1, (level, tileX, tileY) -> level.getTileID(tileX, tileY) == cryptAshID && upgradeShardObject.canPlace(level, tileX, tileY, 0, false) == null, (level, tileX, tileY) -> upgradeShardObject.placeObject(level, tileX, tileY, 0, false));
        GameObject alchemyShardObject = ObjectRegistry.getObject("cryptalchemyshardorerocksmall");
        GenerationTools.generateGuaranteedRandomVeins(this, random, 22, 1, 1, (level, tileX, tileY) -> level.getTileID(tileX, tileY) == cryptAshID && alchemyShardObject.canPlace(level, tileX, tileY, 0, false) == null, (level, tileX, tileY) -> alchemyShardObject.placeObject(level, tileX, tileY, 0, false));
        GenerationTools.checkValid(this);
    }

    @Override
    public LightManager constructLightManager() {
        return new LightManager(this){

            @Override
            public void updateAmbientLight() {
                if (this.ambientLightOverride != null) {
                    this.ambientLight = this.ambientLightOverride;
                    return;
                }
                if (Settings.alwaysLight) {
                    this.ambientLight = this.newLight(150.0f);
                    return;
                }
                this.ambientLight = GraveyardIncursionLevel.this.lightManager.newLight(GRAVEYARD_AMBIENT_LIGHT);
            }
        };
    }

    @Override
    public Stream<WallShadowVariables> getWallShadows() {
        Stream<WallShadowVariables> out = super.getWallShadows();
        float moonLight = this.getWorldEntity().getMoonLightFloat();
        if (moonLight <= 0.0f) {
            return out;
        }
        float moonProgress = this.getWorldEntity().getMoonProgress();
        if (moonProgress <= 0.0f) {
            return out;
        }
        return Stream.concat(out, Stream.of(WallShadowVariables.fromProgress(moonLight, moonProgress, 16.0f, 160.0f)));
    }

    protected static class OpenDirection
    extends Point {
        public int dir;
        public int depth;

        public OpenDirection(int x, int y, int dir, int depth) {
            super(x, y);
            this.dir = dir;
            this.depth = depth;
        }
    }
}

