/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.entity.mobs.Mob;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.LinesGeneration;

public class MinecartsWorldPreset
extends WorldPreset {
    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER);
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = MinecartsWorldPreset.getTotalPoints(random, presetsRegion, 0.02f);
        for (int i = 0; i < total; ++i) {
            ArrayList tntArms;
            LinesGeneration lg;
            Point startTile = MinecartsWorldPreset.findRandomPresetTile(random, presetsRegion, 10, new Dimension(4, 4), (String)null, null);
            if (startTile == null || (lg = Performance.record(performanceTimer, "linesGeneration", () -> this.lambda$addToRegion$0(startTile, random, presetsRegion, tntArms = new ArrayList()))).getRoot().isEmpty()) continue;
            PointHashSet tiles = Performance.record(performanceTimer, "getPoints", lg::getDiamondPoints);
            ArrayList tntTileLines = new ArrayList();
            Performance.record(performanceTimer, "tntLines", () -> {
                int tntCount = random.getOneOfWeighted(Integer.class, 100, 0, 200, 1, 50, 2);
                for (int j = 0; j < tntCount && !tntArms.isEmpty(); ++j) {
                    int tntTileY;
                    int index = random.nextInt(tntArms.size());
                    LinesGeneration next = (LinesGeneration)tntArms.remove(index);
                    int wireLength = random.getIntBetween(10, 14) * random.getOneOf(1, -1);
                    float lineLength = (float)new Point(next.x1, next.y1).distance(next.x2, next.y2);
                    Point2D.Float dir = GameMath.normalize(next.x1 - next.x2, next.y1 - next.y2);
                    float linePointLength = random.getFloatBetween(0.0f, lineLength);
                    Point2D.Float lineTile = new Point2D.Float((float)next.x2 + dir.x * linePointLength, (float)next.y2 + dir.y * linePointLength);
                    Point2D.Float leverTile = GameMath.getPerpendicularPoint(lineTile, 2.0f * Math.signum(wireLength), dir);
                    Point2D.Float tntTile = GameMath.getPerpendicularPoint(lineTile, (float)wireLength, dir);
                    int tntTileX = tntTile.x < 0.0f ? (int)Math.floor(tntTile.x) : (int)tntTile.x;
                    int n = tntTileY = tntTile.y < 0.0f ? (int)Math.floor(tntTile.y) : (int)tntTile.y;
                    if (!this.isTileWithinBounds(tntTileX, tntTileY, presetsRegion, 2)) continue;
                    Line2D.Float wireTileLine = new Line2D.Float(leverTile, tntTile);
                    tntTileLines.add(wireTileLine);
                }
            });
            RegionTileWorldPresetGenerator tileGenerator = new RegionTileWorldPresetGenerator();
            final GameObject crateObject = ObjectRegistry.getObject("crate");
            final int trackObject = ObjectRegistry.getObjectID("minecarttrack");
            Performance.record(performanceTimer, "clearAndPlaceCrates", () -> {
                for (Point tile : tiles) {
                    tileGenerator.addTile(tile.x, tile.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                        @Override
                        public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                            if (level.isSolidTile(tileX, tileY)) {
                                level.setObject(tileX, tileY, 0);
                            }
                            if (random.getChance(0.05) && crateObject.canPlace(level, tileX, tileY, 0, false) == null) {
                                crateObject.placeObject(level, tileX, tileY, 0, false);
                            }
                        }
                    });
                }
            });
            ArrayList trackTiles = new ArrayList();
            Performance.record(performanceTimer, "placeTracks", () -> lg.getRoot().recursiveLines(lg2 -> {
                GameLinkedList minecartTiles = new GameLinkedList();
                LinesGeneration.pathTiles(lg2.getTileLine(), true, (from, next) -> minecartTiles.add(next));
                for (GameLinkedList.Element el : minecartTiles.elements()) {
                    Point current = (Point)el.object;
                    if (!this.isTileWithinBounds(current.x, current.y, presetsRegion, 2)) continue;
                    trackTiles.add(current);
                    GameLinkedList.Element nextEl = el.next();
                    if (nextEl != null) {
                        final Point next2 = (Point)nextEl.object;
                        tileGenerator.addTile(current.x, current.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                            @Override
                            public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                                if (next2.x < tileX) {
                                    level.setObject(tileX, tileY, trackObject, 3);
                                } else if (next2.x > tileX) {
                                    level.setObject(tileX, tileY, trackObject, 1);
                                } else if (next2.y < tileY) {
                                    level.setObject(tileX, tileY, trackObject, 0);
                                } else if (next2.y > tileY) {
                                    level.setObject(tileX, tileY, trackObject, 2);
                                }
                            }
                        });
                        continue;
                    }
                    GameLinkedList.Element prevEl = el.prev();
                    if (prevEl != null) {
                        final Point prev = (Point)prevEl.object;
                        tileGenerator.addTile(current.x, current.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                            @Override
                            public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                                if (prev.x < tileX) {
                                    level.setObject(tileX, tileY, trackObject, 1);
                                } else if (prev.x > tileX) {
                                    level.setObject(tileX, tileY, trackObject, 3);
                                } else if (prev.y < tileY) {
                                    level.setObject(tileX, tileY, trackObject, 2);
                                } else if (prev.y > tileY) {
                                    level.setObject(tileX, tileY, trackObject, 0);
                                }
                            }
                        });
                        continue;
                    }
                    tileGenerator.addTile(current.x, current.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                        @Override
                        public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                            level.setObject(tileX, tileY, trackObject, 0);
                        }
                    });
                }
                return true;
            }));
            Performance.record(performanceTimer, "placeMinecart", () -> {
                int minecartCount = random.getOneOfWeighted(Integer.class, 100, 0, 200, 1, 50, 2);
                for (int j = 0; j < minecartCount && !trackTiles.isEmpty(); ++j) {
                    int index = random.nextInt(trackTiles.size());
                    Point next = (Point)trackTiles.remove(index);
                    tileGenerator.addTile(next.x, next.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                        @Override
                        public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                            Mob minecart = MobRegistry.getMob("minecart", level);
                            level.entityManager.addMob(minecart, tileX * 32 + 16, tileY * 32 + 16);
                        }
                    });
                }
            });
            Performance.record(performanceTimer, "placeTNT", () -> {
                for (Line2D.Float tntLine : tntTileLines) {
                    LinkedList tntTiles = new LinkedList();
                    LinesGeneration.pathTiles(tntLine, true, (fromTile, nextTile) -> tntTiles.add(nextTile));
                    for (Point tile : tntTiles) {
                        tileGenerator.addTile(tile.x, tile.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                            @Override
                            public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                                level.wireManager.setWire(tileX, tileY, 0, true);
                                if (level.getObject((int)tileX, (int)tileY).isSolid) {
                                    level.setObject(tileX, tileY, 0);
                                }
                            }
                        });
                    }
                    Point first = (Point)tntTiles.getFirst();
                    tileGenerator.addTile(first.x, first.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                        @Override
                        public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                            level.setObject(tileX, tileY, ObjectRegistry.getObjectID("rocklever"));
                        }
                    });
                    Point last = (Point)tntTiles.getLast();
                    tileGenerator.addTile(last.x, last.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                        @Override
                        public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                            level.setObject(tileX, tileY, ObjectRegistry.getObjectID("tnt"));
                        }
                    });
                }
            });
            tileGenerator.addToRegion(this, presetsRegion);
        }
    }

    private /* synthetic */ LinesGeneration lambda$addToRegion$0(Point startTile, GameRandom random, LevelPresetsRegion presetsRegion, ArrayList tntArms) {
        LinesGeneration linesGeneration = new LinesGeneration(startTile.x + 2, startTile.y + 2);
        int armAngle = random.nextInt(4) * 90;
        int arms = random.getIntBetween(6, 15);
        for (int j = 0; j < arms; ++j) {
            float width = random.getFloatBetween(1.0f, 3.0f);
            LinesGeneration nextArm = linesGeneration.addArm(random.getIntOffset(armAngle, 20), random.getIntBetween(5, 25), width);
            if (!this.isTileWithinBounds(nextArm.x2, nextArm.y2, presetsRegion, (int)Math.ceil(width) + 1)) {
                linesGeneration.removeLastLine();
                break;
            }
            linesGeneration = nextArm;
            tntArms.add(linesGeneration);
            int angleChange = random.getOneOfWeighted(Integer.class, 30, 0, 5, 90, 5, -90);
            armAngle += angleChange;
        }
        return linesGeneration;
    }
}

