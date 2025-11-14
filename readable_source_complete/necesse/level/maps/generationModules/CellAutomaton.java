/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator;
import necesse.entity.mobs.Mob;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.presets.Preset;

public class CellAutomaton {
    private final PointHashSet alive = new PointHashSet();
    public static final Point[] allNeighbours = new Point[]{new Point(-1, -1), new Point(0, -1), new Point(1, -1), new Point(-1, 0), new Point(1, 0), new Point(-1, 1), new Point(0, 1), new Point(1, 1)};
    public static final Point[] closeNeighbours = new Point[]{new Point(0, -1), new Point(-1, 0), new Point(1, 0), new Point(0, 1)};

    public CellAutomaton setAlive(int x, int y) {
        this.alive.add(x, y);
        return this;
    }

    public CellAutomaton setDead(int x, int y) {
        this.alive.remove(x, y);
        return this;
    }

    public boolean isAlive(int x, int y) {
        return this.alive.contains(x, y);
    }

    private static int countAliveNeighbours(HashMap<Point, Boolean> alive, int x, int y, Point[] neighbours) {
        int count = 0;
        for (Point dp : neighbours) {
            int neighbourX = x + dp.x;
            int neighbourY = y + dp.y;
            if (!alive.getOrDefault(new Point(neighbourX, neighbourY), false).booleanValue()) continue;
            ++count;
        }
        return count;
    }

    public void doCellularAutomaton(int deathLimit, int birthLimit, int iterations, Point[] neighbours) {
        for (int i = 0; i < iterations; ++i) {
            HashMap<Point, Boolean> points = new HashMap<Point, Boolean>();
            for (Point point2 : this.alive) {
                for (int x = -1; x <= 1; ++x) {
                    for (int y = -1; y <= 1; ++y) {
                        int pX = x + point2.x;
                        int pY = y + point2.y;
                        boolean thisAlive = x == 0 && y == 0;
                        points.merge(new Point(pX, pY), thisAlive, (b1, b2) -> b1 != false || b2 != false);
                    }
                }
            }
            this.alive.clear();
            points.forEach((point, isAlive) -> {
                int aliveNeighbours = CellAutomaton.countAliveNeighbours(points, point.x, point.y, neighbours);
                if (isAlive.booleanValue()) {
                    if (aliveNeighbours < deathLimit) {
                        this.alive.remove(point.x, point.y);
                    } else {
                        this.alive.add(point.x, point.y);
                    }
                } else if (aliveNeighbours > birthLimit) {
                    this.alive.add(point.x, point.y);
                }
            });
        }
    }

    public void doCellularAutomaton(int deathLimit, int birthLimit, int iterations) {
        this.doCellularAutomaton(deathLimit, birthLimit, iterations, allNeighbours);
    }

    public void cleanHardEdges() {
        this.doCellularAutomaton(3, Integer.MAX_VALUE, 1, closeNeighbours);
    }

    public Iterable<Point> getAliveUnordered() {
        return this.alive;
    }

    public Stream<Point> streamAliveUnordered() {
        return this.alive.stream();
    }

    public int getAliveCount() {
        return this.alive.size();
    }

    public Stream<Point> streamAliveOrdered() {
        Comparator<Point> comparator = Comparator.comparingInt(e -> e.x);
        comparator = comparator.thenComparingInt(e -> e.y);
        return this.streamAliveUnordered().sorted(comparator);
    }

    public CellAutomaton forEachTile(Level level, GenerationTools.PlaceFunction placeFunction) {
        this.streamAliveOrdered().forEachOrdered(tile -> {
            if (level.isTileWithinBounds(tile.x, tile.y)) {
                placeFunction.place(level, tile.x, tile.y);
            }
        });
        return this;
    }

    public CellAutomaton placeObjects(Level level, int objectID) {
        return this.forEachTile(level, (l, x, y) -> l.setObject(x, y, objectID));
    }

    public CellAutomaton replaceObjects(Level level, int replaceID, int newID) {
        return this.forEachTile(level, (l, x, y) -> {
            if (l.getObjectID(x, y) == replaceID) {
                l.setObject(x, y, newID);
            }
        });
    }

    public CellAutomaton placeTiles(Level level, int tileID) {
        return this.forEachTile(level, (l, x, y) -> l.setTile(x, y, tileID));
    }

    public CellAutomaton replaceTiles(Level level, int replaceID, int newID) {
        return this.forEachTile(level, (l, x, y) -> {
            if (l.getTileID(x, y) == replaceID) {
                l.setTile(x, y, newID);
            }
        });
    }

    public CellAutomaton forEachEdge(Level level, GenerationTools.PlaceFunction placeFunction) {
        return this.forEachTile(level, (l, tileX, tileY) -> {
            if (this.isCellEdge(tileX, tileY)) {
                placeFunction.place(l, tileX, tileY);
            }
        });
    }

    public CellAutomaton placeEdgeWalls(Level level, int wallID, boolean connectEdges) {
        GameObject wall = ObjectRegistry.getObject(wallID);
        this.forEachTile(level, (l, tileX, tileY) -> {
            if (connectEdges ? this.isCellEdgeConnected(tileX, tileY) : this.isCellEdge(tileX, tileY)) {
                l.setObject(tileX, tileY, 0);
                if (wall.canPlace(l, tileX, tileY, 0, false) == null) {
                    wall.placeObject(l, tileX, tileY, 0, false);
                }
            }
        });
        return this;
    }

    public CellAutomaton placeEdgeWalls(RegionTileWorldPresetGenerator tileGenerator, final int wallID, boolean connectEdges) {
        this.streamAliveOrdered().forEach(tile -> {
            if (connectEdges ? this.isCellEdgeConnected(tile.x, tile.y) : this.isCellEdge(tile.x, tile.y)) {
                tileGenerator.addTile(tile.x, tile.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                    @Override
                    public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                        level.setObject(tileX, tileY, wallID);
                    }
                });
            }
        });
        return this;
    }

    public int countAlive(int tileX, int tileY, Point ... neighbours) {
        return (int)Arrays.stream(neighbours).filter(p -> this.isAlive(tileX + p.x, tileY + p.y)).count();
    }

    public int countDead(int tileX, int tileY, Point ... neighbours) {
        return neighbours.length - this.countAlive(tileX, tileY, neighbours);
    }

    public boolean isAllAlive(int tileX, int tileY, Point ... neighbours) {
        return Arrays.stream(neighbours).allMatch(p -> this.isAlive(tileX + p.x, tileY + p.y));
    }

    public boolean isCellEdge(int tileX, int tileY) {
        return !this.isAllAlive(tileX, tileY, closeNeighbours);
    }

    public boolean isCellEdgeConnected(int tileX, int tileY) {
        return !this.isAllAlive(tileX, tileY, allNeighbours);
    }

    public boolean isCellEdge(Point tile) {
        return this.isCellEdge(tile.x, tile.y);
    }

    public void placeFurniturePresets(TicketSystemList<Preset> furniture, float chance, Level level, GameRandom random) {
        this.forEachTile(level, (l, tileX, tileY) -> {
            if (random.getChance(chance)) {
                GenerationTools.generateFurniture(l, random, tileX, tileY, furniture, pos -> pos.objectID() == 0 && this.isAlive(pos.tileX, pos.tileY));
            }
        });
    }

    public void placeFurnitureGettersPresets(RegionTileWorldPresetGenerator tileGenerator, TicketSystemList<Function<GameRandom, Preset>> furniture, float chance) {
        this.streamAliveOrdered().forEach(tile -> tileGenerator.addTile(tile.x, tile.y, (random, level, tileX, tileY, timer) -> Performance.record(timer, "placeFurniture", () -> {
            if (random.getChance(chance)) {
                GenerationTools.generateFurnitureGetters(level, random, tileX, tileY, furniture, pos -> pos.objectID() == 0 && this.isAlive(pos.tileX, pos.tileY));
            }
        })));
    }

    public CellAutomaton spawnMobs(Level level, GameRandom random, String mobStringID, int minTilesPerMob, int maxTilesPerMob, int minMobs, int maxMobs, Predicate<Point> tileFilter) {
        Mob testMob = MobRegistry.getMob(mobStringID, level);
        Point offset = testMob.getPathMoveOffset();
        List spawnPoints = this.streamAliveOrdered().filter(tileFilter).map(tile -> new Point(tile.x * 32 + offset.x, tile.y * 32 + offset.y)).filter(pos -> !testMob.collidesWith(level, pos.x, pos.y)).collect(Collectors.toList());
        int tilesPerMob = random.getIntBetween(minTilesPerMob, maxTilesPerMob);
        int totalMobs = GameMath.limit(spawnPoints.size() / tilesPerMob, minMobs, maxMobs);
        for (int i = 0; i < totalMobs; ++i) {
            Point pos2 = (Point)random.getOneOf(spawnPoints);
            if (pos2 == null) continue;
            Mob mob = MobRegistry.getMob(mobStringID, level);
            mob.onSpawned(pos2.x, pos2.y);
            level.entityManager.addMob(mob, pos2.x, pos2.y);
            mob.canDespawn = false;
        }
        return this;
    }

    public CellAutomaton spawnMobs(Level level, GameRandom random, String mobStringID, int minTilesPerMob, int maxTilesPerMob, int minMobs, int maxMobs) {
        return this.spawnMobs(level, random, mobStringID, minTilesPerMob, maxTilesPerMob, minMobs, maxMobs, tile -> level.getObjectID(tile.x, tile.y) == 0);
    }
}

