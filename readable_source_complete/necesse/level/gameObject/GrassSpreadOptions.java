/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.AreaFinder;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class GrassSpreadOptions {
    public final GameObject object;
    public final Level level;
    private Function<Point, ArrayList<Point>> placePointsGetter;
    private Consumer<Point> placeMethod;
    private Consumer<Point> onPlaced;

    private GrassSpreadOptions(GameObject object, Level level) {
        this.object = object;
        this.level = level;
        this.placeMethod = p -> this.object.placeObject(this.level, p.x, p.y, 0, false);
    }

    public static GrassSpreadOptions init(GameObject object, Level level) {
        return new GrassSpreadOptions(object, level);
    }

    public GrassSpreadOptions placePoints(Function<Point, ArrayList<Point>> getter) {
        this.placePointsGetter = getter;
        return this;
    }

    public GrassSpreadOptions maxSpread(int lookRange, final int maxObjects, int placeRange, final Predicate<Point> isTileObject) {
        return this.placePoints(p -> {
            final AtomicInteger totalObjects = new AtomicInteger(0);
            AreaFinder finder = new AreaFinder(p.x, p.y, lookRange, true){

                @Override
                public boolean checkPoint(int x, int y) {
                    if (isTileObject.test(new Point(x, y))) {
                        return totalObjects.incrementAndGet() > maxObjects;
                    }
                    return false;
                }
            };
            finder.runFinder();
            if (!finder.hasFound()) {
                ArrayList<Point> placePoints = new ArrayList<Point>();
                for (int i = p.x - placeRange; i <= p.x + placeRange; ++i) {
                    for (int j = p.y - placeRange; j <= p.y + placeRange; ++j) {
                        if (i == p.x && j == p.y || this.object.canPlace(this.level, i, j, 0, false) != null) continue;
                        placePoints.add(new Point(i, j));
                    }
                }
                return placePoints;
            }
            return null;
        });
    }

    public GrassSpreadOptions maxSpread(int lookRange, int maxObjects, int placeRange) {
        return this.maxSpread(lookRange, maxObjects, placeRange, p -> this.level.getObjectID(p.x, p.y) == this.object.getID());
    }

    public GrassSpreadOptions placeMethod(Consumer<Point> placeMethod) {
        this.placeMethod = placeMethod;
        return this;
    }

    public GrassSpreadOptions onPlaced(Consumer<Point> onPlaced) {
        this.onPlaced = onPlaced;
        return this;
    }

    private boolean tickSpread(int startTileX, int startTileY, Consumer<Point> additionalOnPlaced) {
        ArrayList<Point> placePoints = this.placePointsGetter.apply(new Point(startTileX, startTileY));
        if (placePoints == null) {
            return false;
        }
        Point placePoint = GameRandom.globalRandom.getOneOf(placePoints);
        if (placePoint != null) {
            this.placeMethod.accept(placePoint);
            if (this.onPlaced != null) {
                this.onPlaced.accept(placePoint);
            }
            if (additionalOnPlaced != null) {
                additionalOnPlaced.accept(placePoint);
            }
            return true;
        }
        return false;
    }

    public boolean tickSpread(int startTileX, int startTileY, boolean sendChangePacket) {
        return this.tickSpread(startTileX, startTileY, sendChangePacket ? p -> this.level.sendObjectUpdatePacket(p.x, p.y) : null);
    }

    public void addSimulateSpread(int startTileX, int startTileY, double spreadChance, long ticks, SimulatePriorityList list, boolean sendChanges) {
        double usedTicks = Math.max(1.0, GameMath.getAverageRunsForSuccess(spreadChance, GameRandom.globalRandom.nextDouble()));
        long remainingTicks = (long)((double)ticks - usedTicks);
        if (remainingTicks > 0L) {
            list.add(startTileX, startTileY, remainingTicks, () -> {
                boolean spread = this.tickSpread(startTileX, startTileY, p -> {
                    if (sendChanges) {
                        this.level.sendObjectUpdatePacket(p.x, p.y);
                    }
                    this.level.getObject(p.x, p.y).addSimulateLogic(this.level, p.x, p.y, remainingTicks, list, sendChanges);
                });
                if (spread) {
                    this.addSimulateSpread(startTileX, startTileY, spreadChance, remainingTicks, list, sendChanges);
                }
            });
        }
    }
}

