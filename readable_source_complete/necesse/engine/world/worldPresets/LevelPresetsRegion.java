/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.registries.WorldPresetRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapArrayList;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.SpawnTileFinder;
import necesse.engine.world.World;
import necesse.engine.world.WorldFile;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.RegionOccupiedRectangles;
import necesse.engine.world.worldPresets.RegionPointsSet;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.engine.world.worldPresets.WorldPresetsRegion;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class LevelPresetsRegion {
    public final WorldPresetsRegion worldRegion;
    public final LevelIdentifier identifier;
    protected HashMap<String, RegionOccupiedRectangles> occupiedRectanglesBoards = new HashMap();
    protected HashMap<Integer, GenerationRegion> generationRegions = new HashMap();
    protected PointHashMap<Integer> generationRegionUniqueIDAtRegion = new PointHashMap();
    protected int generationRegionUniqueIDCounter = 1;
    protected ArrayList<PlaceableWorldPreset> presets = new ArrayList();
    protected HashMapArrayList<Integer, Long> biomeRegions = new HashMapArrayList();
    protected HashMap<Integer, Integer> biomeIDWeights = new HashMap();
    protected int totalBiomeWeight = 0;
    protected HashSet<Integer> currentGenerationRuns = new HashSet();
    protected ArrayList<GeneratedPresetData> generatedPresets = new ArrayList();
    public int unloadBuffer = 0;

    public LevelPresetsRegion(WorldPresetsRegion worldRegion, LevelIdentifier identifier, LevelPresetsRegion existingRegion, PerformanceTimerManager timer) {
        this.worldRegion = worldRegion;
        this.identifier = identifier;
        if (existingRegion != null) {
            Performance.record(timer, "copyBiomes", () -> {
                this.biomeRegions = existingRegion.biomeRegions;
                this.biomeIDWeights = existingRegion.biomeIDWeights;
                this.totalBiomeWeight = existingRegion.totalBiomeWeight;
            });
        } else {
            Performance.record(timer, "getBiomes", () -> {
                int startRegionX = GameMath.getRegionCoordByTile(worldRegion.startTileX);
                int startRegionY = GameMath.getRegionCoordByTile(worldRegion.startTileY);
                int endRegionX = GameMath.getRegionCoordByTile(worldRegion.startTileX + worldRegion.tileWidth - 1);
                int endRegionY = GameMath.getRegionCoordByTile(worldRegion.startTileY + worldRegion.tileHeight - 1);
                BiomeGeneratorStack biomeStack = worldRegion.worldEntity.getGeneratorStack();
                for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
                    for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                        int finalLastBiomeIDCount;
                        int biomeID;
                        int startTileX = GameMath.getTileCoordByRegion(regionX);
                        int startTileY = GameMath.getTileCoordByRegion(regionY);
                        int lastBiomeIDCount = 0;
                        int lastBiomeID = biomeID = biomeStack.getLazyBiomeID(startTileX, startTileY);
                        ++lastBiomeIDCount;
                        this.biomeRegions.add(biomeID, GameMath.getUniqueLongKey(regionX, regionY));
                        biomeID = biomeStack.getLazyBiomeID(startTileX + 16 - 1, startTileY);
                        if (biomeID != lastBiomeID) {
                            finalLastBiomeIDCount = lastBiomeIDCount;
                            this.biomeIDWeights.compute(biomeID, (id, weight) -> weight == null ? finalLastBiomeIDCount : weight + finalLastBiomeIDCount);
                            lastBiomeID = biomeID;
                            lastBiomeIDCount = 1;
                        } else {
                            ++lastBiomeIDCount;
                        }
                        this.biomeRegions.add(biomeID, GameMath.getUniqueLongKey(regionX, regionY));
                        biomeID = biomeStack.getLazyBiomeID(startTileX, startTileY + 16 - 1);
                        if (biomeID != lastBiomeID) {
                            finalLastBiomeIDCount = lastBiomeIDCount;
                            this.biomeIDWeights.compute(biomeID, (id, weight) -> weight == null ? finalLastBiomeIDCount : weight + finalLastBiomeIDCount);
                            lastBiomeID = biomeID;
                            lastBiomeIDCount = 1;
                        } else {
                            ++lastBiomeIDCount;
                        }
                        this.biomeRegions.add(biomeID, GameMath.getUniqueLongKey(regionX, regionY));
                        biomeID = biomeStack.getLazyBiomeID(startTileX + 16 - 1, startTileY + 16 - 1);
                        if (biomeID != lastBiomeID) {
                            finalLastBiomeIDCount = lastBiomeIDCount;
                            this.biomeIDWeights.compute(biomeID, (id, weight) -> weight == null ? finalLastBiomeIDCount : weight + finalLastBiomeIDCount);
                            lastBiomeID = biomeID;
                            lastBiomeIDCount = 1;
                        }
                        this.biomeRegions.add(biomeID, GameMath.getUniqueLongKey(regionX, regionY));
                        int finalLastBiomeIDCount2 = ++lastBiomeIDCount;
                        this.biomeIDWeights.compute(lastBiomeID, (id, weight) -> weight == null ? finalLastBiomeIDCount2 : weight + finalLastBiomeIDCount2);
                        this.totalBiomeWeight += 4;
                    }
                }
            });
        }
    }

    public synchronized void addOccupiedSpaceBoard(String board, Rectangle tileRectangle) {
        this.occupiedRectanglesBoards.compute(board, (key, last) -> {
            if (last == null) {
                last = new RegionOccupiedRectangles();
            }
            return last;
        }).add(tileRectangle);
    }

    public synchronized PlaceableWorldPreset addPreset(WorldPreset preset, Iterable<Rectangle> occupiedTileRectangles, String[] addToOccupiedSpaceBoards, WorldPresetPlaceFunction placeFunction) {
        RegionPointsSet regions = new RegionPointsSet();
        for (Rectangle occupiedTileRectangle : occupiedTileRectangles) {
            regions.addTileRectangle(occupiedTileRectangle);
            if (addToOccupiedSpaceBoards == null) continue;
            for (String board : addToOccupiedSpaceBoards) {
                this.addOccupiedSpaceBoard(board, occupiedTileRectangle);
            }
        }
        TiledPlaceableWorldPreset worldPreset = new TiledPlaceableWorldPreset(this, preset, this.presets.size(), regions, occupiedTileRectangles, placeFunction);
        this.addPresetToGenerationRegion(worldPreset);
        this.presets.add(worldPreset);
        return worldPreset;
    }

    public PlaceableWorldPreset addPreset(WorldPreset preset, final Rectangle occupiedRectangle, String[] addToOccupiedSpaceBoards, WorldPresetPlaceFunction placeFunction) {
        LinkedList<Rectangle> iterable = new LinkedList<Rectangle>(){
            {
                this.add(occupiedRectangle);
            }
        };
        return this.addPreset(preset, (Iterable<Rectangle>)iterable, addToOccupiedSpaceBoards, placeFunction);
    }

    public PlaceableWorldPreset addPreset(WorldPreset preset, int tileX, int tileY, Dimension dimension, String[] addToOccupiedSpaceBoards, WorldPresetPlaceFunction placeFunction) {
        return this.addPreset(preset, new Rectangle(tileX, tileY, dimension.width, dimension.height), addToOccupiedSpaceBoards, placeFunction);
    }

    public PlaceableWorldPreset addPreset(WorldPreset preset, Iterable<Rectangle> occupiedTileRectangles, String addToOccupiedSpaceBoard, WorldPresetPlaceFunction placeFunction) {
        String[] stringArray;
        if (addToOccupiedSpaceBoard == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = addToOccupiedSpaceBoard;
        }
        return this.addPreset(preset, occupiedTileRectangles, stringArray, placeFunction);
    }

    public PlaceableWorldPreset addPreset(WorldPreset preset, Rectangle occupiedRectangle, String addToOccupiedSpaceBoard, WorldPresetPlaceFunction placeFunction) {
        String[] stringArray;
        if (addToOccupiedSpaceBoard == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = addToOccupiedSpaceBoard;
        }
        return this.addPreset(preset, occupiedRectangle, stringArray, placeFunction);
    }

    public PlaceableWorldPreset addPreset(WorldPreset preset, int tileX, int tileY, Dimension dimension, String addToOccupiedSpaceBoard, WorldPresetPlaceFunction placeFunction) {
        String[] stringArray;
        if (addToOccupiedSpaceBoard == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = addToOccupiedSpaceBoard;
        }
        return this.addPreset(preset, tileX, tileY, dimension, stringArray, placeFunction);
    }

    public synchronized PlaceableWorldPreset addPresetToRegions(WorldPreset preset, RegionPointsSet regions, WorldPresetPlaceFunction placeFunction) {
        RegionPlaceableWorldPreset worldPreset = new RegionPlaceableWorldPreset(this, preset, this.presets.size(), regions, placeFunction);
        this.addPresetToGenerationRegion(worldPreset);
        this.presets.add(worldPreset);
        return worldPreset;
    }

    private void addPresetToGenerationRegion(PlaceableWorldPreset worldPreset) {
        GenerationRegion generationRegion = null;
        for (Point region : worldPreset.occupiedRegions) {
            int regionUniqueID = this.generationRegionUniqueIDAtRegion.getOrDefault(region.x, region.y, -1);
            if (regionUniqueID == -1) {
                if (generationRegion == null) {
                    generationRegion = new GenerationRegion(this.generationRegionUniqueIDCounter++);
                    this.generationRegionUniqueIDAtRegion.put(region.x, region.y, generationRegion.uniqueID);
                    this.generationRegions.put(generationRegion.uniqueID, generationRegion);
                } else {
                    this.generationRegionUniqueIDAtRegion.put(region.x, region.y, generationRegion.uniqueID);
                }
            } else {
                GenerationRegion existingGenerationRegion = this.generationRegions.get(regionUniqueID);
                if (existingGenerationRegion != generationRegion && generationRegion != null) {
                    existingGenerationRegion.presets.addAll(generationRegion.presets);
                    existingGenerationRegion.regions.addAll(generationRegion.regions);
                    this.generationRegions.remove(generationRegion.uniqueID);
                    for (Point point : generationRegion.regions) {
                        this.generationRegionUniqueIDAtRegion.put(point.x, point.y, existingGenerationRegion.uniqueID);
                    }
                    for (PlaceableWorldPreset preset : generationRegion.presets) {
                        preset.generationRegionUniqueID = existingGenerationRegion.uniqueID;
                    }
                }
                generationRegion = existingGenerationRegion;
            }
            generationRegion.regions.add(region.x, region.y);
        }
        if (generationRegion == null) {
            throw new IllegalStateException("Attempted to add world preset without any occupied regions: " + worldPreset.preset.getStringID());
        }
        worldPreset.generationRegionUniqueID = generationRegion.uniqueID;
        generationRegion.presets.add(worldPreset);
    }

    public boolean isRectangleOccupied(String board, Rectangle tileRectangle) {
        RegionOccupiedRectangles occupiedRectangles = this.occupiedRectanglesBoards.get(board);
        return occupiedRectangles != null && occupiedRectangles.isOccupied(tileRectangle);
    }

    public boolean isRectangleOccupied(String board, int tileX, int tileY, int width, int height) {
        return this.isRectangleOccupied(board, new Rectangle(tileX, tileY, width, height));
    }

    public boolean isRectangleOccupied(String[] boards, Rectangle tileRectangle) {
        return Arrays.stream(boards).anyMatch(board -> this.isRectangleOccupied((String)board, tileRectangle));
    }

    public boolean isRectangleOccupied(String[] boards, int tileX, int tileY, int width, int height) {
        return this.isRectangleOccupied(boards, new Rectangle(tileX, tileY, width, height));
    }

    public boolean hasAnyOfBiome(int biomeID) {
        return !this.biomeRegions.isEmpty(biomeID);
    }

    public float getBiomeWeight(int biomeID) {
        Integer weight = this.biomeIDWeights.get(biomeID);
        if (weight == null) {
            return 0.0f;
        }
        return (float)weight.intValue() / (float)this.totalBiomeWeight;
    }

    public Iterable<Point> getBiomeRegions(int biomeID) {
        ArrayList regionUniqueIDs = (ArrayList)this.biomeRegions.get(biomeID);
        return GameUtils.mapIterable(regionUniqueIDs.iterator(), uniqueKey -> {
            int regionX = GameMath.getXFromUniqueLongKey(uniqueKey);
            int regionY = GameMath.getYFromUniqueLongKey(uniqueKey);
            return new Point(regionX, regionY);
        });
    }

    public boolean isRegionBiome(int regionX, int regionY, int biomeID) {
        return this.biomeRegions.contains(biomeID, GameMath.getUniqueLongKey(regionX, regionY));
    }

    public Point getRandomBiomeRegion(GameRandom random, int biomeID) {
        ArrayList regionUniqueIDs = (ArrayList)this.biomeRegions.get(biomeID);
        if (regionUniqueIDs.isEmpty()) {
            return null;
        }
        long regionUniqueID = (Long)random.getOneOf(regionUniqueIDs);
        int regionX = GameMath.getXFromUniqueLongKey(regionUniqueID);
        int regionY = GameMath.getYFromUniqueLongKey(regionUniqueID);
        return new Point(regionX, regionY);
    }

    public Point getRandomBiomeRegion(GameRandom random, Iterable<Integer> biomeIDs) {
        TicketSystemList biomeRegionsList = new TicketSystemList();
        for (int biomeID : biomeIDs) {
            ArrayList regionUniqueIDs = (ArrayList)this.biomeRegions.get(biomeID);
            if (regionUniqueIDs.isEmpty()) continue;
            biomeRegionsList.addObject(regionUniqueIDs.size(), regionUniqueIDs);
        }
        ArrayList regionUniqueIDs = (ArrayList)biomeRegionsList.getRandomObject(random);
        if (regionUniqueIDs.isEmpty()) {
            return null;
        }
        long regionUniqueID = (Long)random.getOneOf(regionUniqueIDs);
        int regionX = GameMath.getXFromUniqueLongKey(regionUniqueID);
        int regionY = GameMath.getYFromUniqueLongKey(regionUniqueID);
        return new Point(regionX, regionY);
    }

    public void finalizeRegionSetup() {
        for (GenerationRegion generationRegion : this.generationRegions.values()) {
            Comparator<PlaceableWorldPreset> presetsComparator = Comparator.comparingInt(preset -> -preset.preset.getPriority());
            presetsComparator = presetsComparator.thenComparingInt(preset -> preset.index);
            generationRegion.presets.sort(presetsComparator);
        }
    }

    protected GenerationRegion getGenerationRegionAtRegion(int regionX, int regionY) {
        int generationRegionUniqueID = this.generationRegionUniqueIDAtRegion.getOrDefault(regionX, regionY, -1);
        if (generationRegionUniqueID == -1) {
            return null;
        }
        return this.generationRegions.get(generationRegionUniqueID);
    }

    public synchronized int startGenerateRegion(Region region) {
        GenerationRegion generationRegion = this.getGenerationRegionAtRegion(region.regionX, region.regionY);
        if (generationRegion == null) {
            return -1;
        }
        if (this.currentGenerationRuns.contains(generationRegion.uniqueID)) {
            return -1;
        }
        if (WorldPresetsRegion.DEBUG_PLACING_PRESETS && !this.currentGenerationRuns.isEmpty()) {
            Object[] otherGenerationRuns = (String[])this.currentGenerationRuns.stream().map(id -> this.generationRegions.get(id)).map(r -> {
                String regionString = r.regions.stream().findFirst().map(pos -> pos.x + "x" + pos.y).orElse(null);
                return r.uniqueID + " (" + regionString + ")";
            }).toArray(String[]::new);
            String regionString = generationRegion.regions.stream().findFirst().map(pos -> pos.x + "x" + pos.y).orElse(null);
            System.out.println("ALREADY RUNNING ANOTHER GENERATION RUN OTHER THAN " + generationRegion.uniqueID + " (" + regionString + ")?? " + Arrays.toString(otherGenerationRuns));
        }
        this.currentGenerationRuns.add(generationRegion.uniqueID);
        block0: for (PlaceableWorldPreset preset : generationRegion.presets) {
            for (Point regionPos : preset.occupiedRegions) {
                if (regionPos.x == region.regionX && regionPos.y == region.regionY || !region.manager.level.regionManager.isRegionGenerated(regionPos.x, regionPos.y)) continue;
                preset.hasAlreadyGeneratedRegion = true;
                if (!WorldPresetsRegion.DEBUG_PLACING_PRESETS) continue block0;
                System.out.println("ALREADY GENERATED " + preset.getDebugName() + ", REGION: " + regionPos.x + "x" + regionPos.y + ", STARTED FROM: " + region.regionX + "x" + region.regionY + ", RUN: " + generationRegion.uniqueID);
                continue block0;
            }
        }
        return generationRegion.uniqueID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void runGenerateRegion(int generationUniqueID, Region region, int customSeed) {
        if (generationUniqueID == -1) {
            return;
        }
        GenerationRegion generationRegion = this.generationRegions.get(generationUniqueID);
        try {
            Level level = region.manager.level;
            boolean debugTimer = WorldPresetsRegion.DEBUG_PLACING_PRESETS;
            PerformanceTimerManager timer = debugTimer ? new PerformanceTimerManager(false) : (this.worldRegion.worldEntity.isServer() ? this.worldRegion.worldEntity.getServer().tickManager() : (this.worldRegion.worldEntity.isClient() ? this.worldRegion.worldEntity.getClient().tickManager() : null));
            long time = System.nanoTime();
            for (Iterator<PlaceableWorldPreset> regionPos : generationRegion.regions) {
                if (((Point)((Object)regionPos)).x == region.regionX && ((Point)((Object)regionPos)).y == region.regionY || !this.worldRegion.isRegionWithinBounds(((Point)((Object)regionPos)).x, ((Point)((Object)regionPos)).y)) continue;
                Region newRegion = region.getRegion(((Point)((Object)regionPos)).x, ((Point)((Object)regionPos)).y, true);
                if (!this.worldRegion.worldEntity.keepPresetGeneratedRegionsLoaded) continue;
                newRegion.unloadRegionBuffer = region.unloadRegionBuffer;
            }
            Performance.addRecordedTime(timer, "generateRegions", System.nanoTime() - time);
            boolean placeAnyPresets = Performance.record(timer, "placePresets", () -> {
                boolean placedAnyPresets = false;
                for (PlaceableWorldPreset worldPreset : generationRegion.presets) {
                    if (worldPreset.hasAlreadyGeneratedRegion) continue;
                    placedAnyPresets = true;
                    Performance.record(timer, worldPreset.preset.getStringID(), () -> {
                        GameRandom random = customSeed == 0 ? this.worldRegion.worldEntity.getNewWorldRandom() : new GameRandom(customSeed);
                        random = random.nextSeeded(worldPreset.firstRegion.x).nextSeeded(worldPreset.firstRegion.y).nextSeeded(worldPreset.index);
                        try {
                            FoundPresetData presetData;
                            worldPreset.place(random, level, timer);
                            if (worldPreset.preset.shouldSaveGenerated && worldPreset instanceof FoundPresetData && (presetData = (FoundPresetData)((Object)worldPreset)).getTileX() != Integer.MIN_VALUE && presetData.getTileY() != Integer.MIN_VALUE) {
                                this.generatedPresets.add(new GeneratedPresetData(presetData));
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                return placedAnyPresets;
            });
            for (Point regionPos : generationRegion.regions) {
                if (!this.worldRegion.isRegionWithinBounds(regionPos.x, regionPos.y)) continue;
                level.removeDirtyRegion(regionPos.x, regionPos.y);
            }
            for (PlaceableWorldPreset worldPreset : generationRegion.presets) {
                if (worldPreset.hasAlreadyGeneratedRegion) continue;
                Performance.record(timer, "validateTiles", () -> {
                    for (Rectangle tileRectangle : worldPreset.getOccupiedTileRectangles()) {
                        Region.checkTilesGenerationValid(level, tileRectangle);
                    }
                });
            }
            if (debugTimer && placeAnyPresets) {
                String regionString = generationRegion.regions.stream().findFirst().map(pos -> pos.x + "x" + pos.y).orElse(null);
                System.out.println("PERFORMANCE RESULTS FOR PLACING PRESETS GENERATION " + generationRegion.uniqueID + " (" + regionString + "):");
                System.out.println("TOTAL GENERATED REGIONS: " + generationRegion.regions.size());
                PerformanceTimerUtils.printPerformanceTimer(timer.getCurrentRootPerformanceTimer());
            }
        }
        finally {
            this.currentGenerationRuns.remove(generationRegion.uniqueID);
            region.manager.level.runDirtyRegionSync();
        }
    }

    public Stream<FoundPresetData> streamPresets(Predicate<PlaceableWorldPreset> filter) {
        return Stream.concat(this.generatedPresets.stream(), this.presets.stream().filter(preset -> preset instanceof FoundPresetData).filter(filter).map(preset -> (FoundPresetData)((Object)preset)));
    }

    public void loadGeneratedPresetsFile() {
        World world = this.worldRegion.worldEntity.serverWorld;
        if (world != null) {
            WorldFile file = world.fileSystem.getPresetRegionFile(this.identifier, this.worldRegion.worldPresetRegionX, this.worldRegion.worldPresetRegionY);
            try {
                if (file.exists()) {
                    LoadData save = new LoadData(file);
                    this.generatedPresets.clear();
                    for (LoadData presetSave : save.getLoadDataByName("PRESET")) {
                        try {
                            this.generatedPresets.add(new GeneratedPresetData(presetSave));
                        }
                        catch (LoadDataException e) {
                            GameLog.warn.println("Error loading generated preset: " + e.getMessage());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e) {
                System.err.println("Could not load " + this.identifier + " generated presets from file at " + this.worldRegion.startLevelRegionX + "x" + this.worldRegion.startLevelRegionY + " (" + this.worldRegion.worldPresetRegionX + "x" + this.worldRegion.worldPresetRegionY + ")");
                e.printStackTrace();
            }
        }
    }

    public void saveGeneratedPresetsFile() {
        World world = this.worldRegion.worldEntity.serverWorld;
        if (world != null && !this.generatedPresets.isEmpty()) {
            WorldFile file = world.fileSystem.getPresetRegionFile(this.identifier, this.worldRegion.worldPresetRegionX, this.worldRegion.worldPresetRegionY);
            SaveData save = new SaveData("");
            for (GeneratedPresetData presetData : this.generatedPresets) {
                if (!presetData.getPreset().shouldSaveGenerated) continue;
                save.addSaveData(presetData.getSaveData("PRESET"));
            }
            save.saveScript(file);
        }
    }

    public Iterable<PresetDebugData> getDebugData() {
        return GameUtils.mapIterable(this.presets.iterator(), preset -> preset);
    }

    public Iterable<GenerationRegionDebugData> getDebugDataByGenerationRegions() {
        return GameUtils.mapIterable(this.generationRegions.values().iterator(), generationRegion -> generationRegion);
    }

    public void markPresetsToNotGenerate(Predicate<PlaceableWorldPreset> filter) {
        for (PlaceableWorldPreset preset : this.presets) {
            if (!filter.test(preset)) continue;
            preset.hasAlreadyGeneratedRegion = true;
        }
    }

    protected static class TiledPlaceableWorldPreset
    extends PlaceableWorldPreset
    implements FoundPresetData {
        public final Iterable<Rectangle> occupiedTileRectangles;
        public final int averageTileX;
        public final int averageTileY;
        public final WorldPresetPlaceFunction placeFunction;

        public TiledPlaceableWorldPreset(LevelPresetsRegion presetsRegion, WorldPreset worldPreset, int index, RegionPointsSet occupiedRegions, Iterable<Rectangle> occupiedTileRectangles, WorldPresetPlaceFunction placeFunction) {
            super(presetsRegion, worldPreset, index, occupiedRegions);
            this.occupiedTileRectangles = occupiedTileRectangles;
            int totalTileX = 0;
            int totalTileY = 0;
            int count = 0;
            for (Rectangle tileRectangle : occupiedTileRectangles) {
                totalTileX += tileRectangle.x + tileRectangle.width / 2;
                totalTileY += tileRectangle.y + tileRectangle.height / 2;
                ++count;
            }
            this.averageTileX = count > 0 ? totalTileX / count : Integer.MIN_VALUE;
            this.averageTileY = count > 0 ? totalTileY / count : Integer.MIN_VALUE;
            this.placeFunction = placeFunction;
        }

        @Override
        public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
            this.placeFunction.place(random, level, timer);
        }

        @Override
        public Iterable<Rectangle> getOccupiedTileRectangles() {
            return this.occupiedTileRectangles;
        }

        @Override
        public int getTileX() {
            return this.averageTileX;
        }

        @Override
        public int getTileY() {
            return this.averageTileY;
        }

        @Override
        public WorldPreset getPreset() {
            return this.preset;
        }

        @Override
        public boolean isGenerated() {
            return false;
        }
    }

    public static interface WorldPresetPlaceFunction {
        public void place(GameRandom var1, Level var2, PerformanceTimerManager var3);
    }

    public static abstract class PlaceableWorldPreset
    implements PresetDebugData {
        public final WorldPreset preset;
        protected int generationRegionUniqueID = -1;
        public final int index;
        public final RegionPointsSet occupiedRegions;
        public final Point firstRegion;
        protected String debugName;
        protected boolean hasAlreadyGeneratedRegion = false;
        protected int removeIfWithinSpawnRegionRange;

        protected PlaceableWorldPreset(LevelPresetsRegion presetsRegion, WorldPreset worldPreset, int index, RegionPointsSet occupiedRegions) {
            this.preset = worldPreset;
            this.index = index;
            this.occupiedRegions = occupiedRegions;
            this.removeIfWithinSpawnRegionRange = presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER) ? SpawnTileFinder.CLEAR_SPAWN_REGION_RANGE : -1;
            Comparator<Point> comparator = Comparator.comparingInt(e -> e.x);
            comparator = comparator.thenComparingInt(e -> e.y);
            this.firstRegion = occupiedRegions.streamRegions().min(comparator).orElse(null);
            if (this.firstRegion == null) {
                throw new IllegalStateException("WorldPreset must have at least one occupied region, but none were found for preset: " + worldPreset.getStringID());
            }
        }

        public PlaceableWorldPreset setDebugName(String debugName) {
            this.debugName = debugName;
            return this;
        }

        public PlaceableWorldPreset setDontRemoveIfNearbySpawn() {
            this.removeIfWithinSpawnRegionRange = 0;
            return this;
        }

        public PlaceableWorldPreset setRemoveIfWithinSpawnRegionRange(int regionRange) {
            this.removeIfWithinSpawnRegionRange = regionRange;
            return this;
        }

        public int getRemoveIfWithinSpawnRegionRange() {
            return this.removeIfWithinSpawnRegionRange;
        }

        public abstract void place(GameRandom var1, Level var2, PerformanceTimerManager var3);

        @Override
        public String getDebugName() {
            return this.preset.getStringID() + ":" + this.index + "@" + Integer.toHexString(this.hashCode()) + (this.debugName == null ? "" : "\n" + this.debugName);
        }

        @Override
        public Iterable<Point> getOccupiedRegions() {
            return this.occupiedRegions;
        }

        @Override
        public int getGenerationRegionUniqueID() {
            return this.generationRegionUniqueID;
        }
    }

    protected static class RegionPlaceableWorldPreset
    extends PlaceableWorldPreset {
        public final WorldPresetPlaceFunction placeFunction;

        public RegionPlaceableWorldPreset(LevelPresetsRegion presetsRegion, WorldPreset worldPreset, int index, RegionPointsSet occupiedRegions, WorldPresetPlaceFunction placeFunction) {
            super(presetsRegion, worldPreset, index, occupiedRegions);
            this.placeFunction = placeFunction;
        }

        @Override
        public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
            this.placeFunction.place(random, level, timer);
        }

        @Override
        public Iterable<Rectangle> getOccupiedTileRectangles() {
            return Collections.emptyList();
        }
    }

    protected static class GenerationRegion
    implements GenerationRegionDebugData {
        public final int uniqueID;
        public final PointHashSet regions = new PointHashSet();
        public final ArrayList<PlaceableWorldPreset> presets = new ArrayList();

        public GenerationRegion(int uniqueID) {
            this.uniqueID = uniqueID;
        }

        @Override
        public int getUniqueID() {
            return this.uniqueID;
        }

        @Override
        public Iterable<PresetDebugData> getPresetDebugData() {
            return GameUtils.mapIterable(this.presets.iterator(), preset -> preset);
        }

        @Override
        public Iterable<Point> getRegions() {
            return this.regions;
        }
    }

    protected static class GeneratedPresetData
    implements FoundPresetData {
        public final WorldPreset preset;
        public final int tileX;
        public final int tileY;

        protected GeneratedPresetData(FoundPresetData presetData) {
            this.preset = presetData.getPreset();
            this.tileX = presetData.getTileX();
            this.tileY = presetData.getTileY();
        }

        protected GeneratedPresetData(LoadData save) throws LoadDataException {
            String presetStringID = save.getUnsafeString("presetStringID", null, false);
            if (presetStringID == null) {
                throw new LoadDataException("Missing generated presetStringID");
            }
            this.preset = WorldPresetRegistry.getPreset(presetStringID);
            if (this.preset == null) {
                throw new LoadDataException("Invalid generated presetStringID: " + presetStringID);
            }
            this.tileX = save.getInt("tileX", Integer.MIN_VALUE, false);
            this.tileY = save.getInt("tileY", Integer.MIN_VALUE, false);
            if (this.tileX == Integer.MIN_VALUE || this.tileY == Integer.MIN_VALUE) {
                throw new LoadDataException("Missing generated preset tile");
            }
        }

        public SaveData getSaveData(String name) {
            SaveData save = new SaveData(name);
            save.addUnsafeString("presetStringID", this.preset.getStringID());
            save.addInt("tileX", this.tileX);
            save.addInt("tileY", this.tileY);
            return save;
        }

        @Override
        public int getTileX() {
            return this.tileX;
        }

        @Override
        public int getTileY() {
            return this.tileY;
        }

        @Override
        public WorldPreset getPreset() {
            return this.preset;
        }

        @Override
        public boolean isGenerated() {
            return true;
        }
    }

    public static interface FoundPresetData {
        public int getTileX();

        public int getTileY();

        public WorldPreset getPreset();

        public boolean isGenerated();
    }

    public static interface PresetDebugData {
        public String getDebugName();

        public Iterable<Point> getOccupiedRegions();

        public Iterable<Rectangle> getOccupiedTileRectangles();

        public int getGenerationRegionUniqueID();
    }

    public static interface GenerationRegionDebugData {
        public int getUniqueID();

        public Iterable<PresetDebugData> getPresetDebugData();

        public Iterable<Point> getRegions();
    }
}

