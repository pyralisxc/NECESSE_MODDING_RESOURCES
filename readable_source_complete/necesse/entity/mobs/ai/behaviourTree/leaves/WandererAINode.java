/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.util.WandererBaseOptions;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.SubRegion;

public class WandererAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public int frequency;
    public int randomMod = 0;
    public int searchRadius;
    public Predicate<T> hideInside;
    public boolean runAwayFromAttacker = true;
    public boolean runAwayFromAttackerToBase;
    public Function<T, ZoneTester> getZoneTester;
    private boolean isRunningAway;
    public String baseOptionsKey = "baseOptions";

    public WandererAINode(int wanderFrequency) {
        this.frequency = wanderFrequency;
        this.searchRadius = 10;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        Point base;
        WandererBaseOptions<T> baseOptions;
        blackboard.onEvent("wanderNow", e -> {
            this.randomMod = this.frequency;
        });
        if (this.hideInside != null && (baseOptions = this.getBaseOptions()) != null && (base = baseOptions.getBaseTile(mob)) != null && this.hideInside.test(mob)) {
            boolean isBaseInside;
            boolean bl = isBaseInside = !((Entity)mob).getLevel().isOutside(base.x, base.y);
            if (isBaseInside) {
                ConnectedSubRegionsResult regions = null;
                if (baseOptions.isBaseRoom(mob, base)) {
                    regions = ((Entity)mob).getLevel().regionManager.getRoomConnectedByTile(base.x, base.y, true, 2000);
                } else if (baseOptions.isBaseHouse(mob, base)) {
                    regions = ((Entity)mob).getLevel().regionManager.getHouseConnectedByTile(base.x, base.y, 4000);
                }
                if (regions != null) {
                    int tileX = ((Entity)mob).getTileX();
                    int tileY = ((Entity)mob).getTileY();
                    if (regions.connectedRegions.stream().anyMatch(r -> r.hasLevelTile(tileX, tileY))) {
                        this.getBlackboard().put("isHidingInside", true);
                    }
                }
            } else {
                int tileY;
                int radius = baseOptions.getBaseRadius(mob, this);
                int tileX = ((Entity)mob).getTileX();
                if (GameMath.preciseDistance(tileX, tileY = ((Entity)mob).getTileY(), base.x, base.y) <= (float)radius) {
                    this.getBlackboard().put("isHidingInside", true);
                }
            }
        }
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        WandererBaseOptions<T> baseOptions;
        if (this.runAwayFromAttacker || this.runAwayFromAttackerToBase) {
            Point base;
            baseOptions = this.getBaseOptions();
            Point point = this.runAwayFromAttackerToBase ? (baseOptions == null ? null : baseOptions.getBaseTile(mob)) : (base = null);
            if (base != null || this.runAwayFromAttacker) {
                for (AIWasHitEvent e : blackboard.getLastHits()) {
                    int yOffset;
                    Mob attackOwner = e.event.attacker != null ? e.event.attacker.getAttackOwner() : null;
                    if (attackOwner == null) continue;
                    if (base != null) {
                        Point housePoint = this.findNewPositionInsideBase(mob, base, (arg_0, arg_1) -> mob.getTileWanderPriority(arg_0, arg_1), baseOptions);
                        if (housePoint != null) {
                            return this.moveToTileTask(housePoint.x, housePoint.y, null, path -> {
                                path.moveIfWithin(-1, -1, null);
                                return AINodeResult.SUCCESS;
                            });
                        }
                    }
                    if (!this.runAwayFromAttacker) break;
                    Point2D.Float dir = GameMath.normalize(((Mob)mob).x - attackOwner.x, ((Mob)mob).y - attackOwner.y);
                    float dirMod = Math.abs(dir.x) > Math.abs(dir.y) ? 1.0f / Math.abs(dir.x) : 1.0f / Math.abs(dir.y);
                    dir.x *= dirMod;
                    dir.y *= dirMod;
                    int baseRadius = baseOptions == null ? this.searchRadius : baseOptions.getBaseRadius(mob, this);
                    int xOffset = (int)(dir.x * (float)baseRadius);
                    Point nextPoint = this.findNewPosition(mob, xOffset, yOffset = (int)(dir.y * (float)baseRadius), baseOptions);
                    if (nextPoint == null) break;
                    this.isRunningAway = true;
                    return this.moveToTileTask(nextPoint.x, nextPoint.y, null, path -> {
                        path.moveIfWithin(-1, -1, null);
                        return AINodeResult.SUCCESS;
                    });
                }
            }
        }
        if (blackboard.mover.hasMobTarget() && !blackboard.mover.isCurrentlyMovingFor(this)) {
            Mob target = blackboard.mover.getTargetMob();
            blackboard.mover.directMoveTo(this, target.getX(), target.getY());
        }
        if (!blackboard.mover.isMoving() && this.isRunningAway) {
            blackboard.submitEvent("ranAway", new AIEvent());
            this.isRunningAway = false;
        }
        if (!blackboard.mover.isMoving() || !blackboard.mover.isCurrentlyMovingFor(this)) {
            if (this.frequency - this.randomMod <= 0 || GameRandom.globalRandom.nextInt(this.frequency - this.randomMod) < 50) {
                baseOptions = this.getBaseOptions();
                Point nextPoint = this.findNewPosition(mob, baseOptions);
                if (nextPoint == null) {
                    nextPoint = this.findNewPositionInsideBase(mob, baseOptions == null ? null : baseOptions.getBaseTile(mob), baseOptions);
                }
                if (nextPoint != null) {
                    return this.moveToTileTask(nextPoint.x, nextPoint.y, null, path -> {
                        path.moveIfWithin(-1, -1, null);
                        return AINodeResult.SUCCESS;
                    });
                }
            } else {
                this.randomMod += 50;
            }
        }
        return AINodeResult.SUCCESS;
    }

    public WandererBaseOptions<T> getBaseOptions() {
        WandererBaseOptions baseOptions = this.getBlackboard().getObject(WandererBaseOptions.class, this.baseOptionsKey);
        if (baseOptions != null) {
            return baseOptions;
        }
        Point wanderBaseTile = ((Mob)this.mob()).getWanderBaseTile();
        if (wanderBaseTile != null) {
            return mob -> wanderBaseTile;
        }
        return null;
    }

    public Point findNewPosition(T mob, WandererBaseOptions<T> baseOptions) {
        return this.findNewPosition(mob, 0, 0, baseOptions);
    }

    public Point findNewPosition(T mob, int xOffset, int yOffset, WandererBaseOptions<T> baseOptions) {
        return this.findNewPosition(mob, xOffset, yOffset, baseOptions, (arg_0, arg_1) -> mob.getTileWanderPriority(arg_0, arg_1));
    }

    public Point findNewPosition(T mob, int xOffset, int yOffset, WandererBaseOptions<T> baseOptions, BiFunction<TilePosition, Biome, Integer> tilePriority) {
        int baseRadius;
        Rectangle baseRect;
        Point baseTile;
        this.randomMod = 0;
        this.getBlackboard().put("isHidingInside", false);
        Point point = baseTile = baseOptions == null ? null : baseOptions.getBaseTile(mob);
        if (baseTile != null && this.hideInside != null && this.hideInside.test(mob)) {
            Point housePoint = this.findNewPositionInsideBase(mob, baseTile, tilePriority, baseOptions);
            if (housePoint != null) {
                this.getBlackboard().put("isHidingInside", true);
            }
            return housePoint;
        }
        int mobTileX = ((Entity)mob).getTileX();
        int mobTileY = ((Entity)mob).getTileY();
        if (baseTile != null && !(baseRect = new Rectangle(baseTile.x - (baseRadius = baseOptions.getBaseRadius(mob, this)), baseTile.y - baseRadius, baseRadius * 2, baseRadius * 2)).contains(mobTileX, mobTileY)) {
            xOffset = (int)Math.signum(baseTile.x - mobTileX) * baseRadius / 2;
            yOffset = (int)Math.signum(baseTile.y - mobTileY) * baseRadius / 2;
        }
        ZoneTester zoneTester = null;
        if (this.getZoneTester != null) {
            zoneTester = this.getZoneTester.apply(mob);
        }
        if (baseTile != null && baseOptions.forceFindAroundBase(mob)) {
            return WandererAINode.findWanderingPointAround(mob, baseTile.x, baseTile.y, baseOptions.getBaseRadius(mob, this), zoneTester, tilePriority, 20, 5);
        }
        return WandererAINode.findWanderingPoint(mob, xOffset, yOffset, baseOptions == null ? this.searchRadius : baseOptions.getBaseRadius(mob, this), zoneTester, tilePriority, 20, 5);
    }

    public Point findNewPositionInsideBase(T mob, Point baseTile, WandererBaseOptions<T> baseOptions) {
        return this.findNewPositionInsideBase(mob, baseTile, (arg_0, arg_1) -> mob.getTileWanderPriority(arg_0, arg_1), baseOptions);
    }

    public Point findNewPositionInsideBase(T mob, Point baseTile, BiFunction<TilePosition, Biome, Integer> tilePriority, WandererBaseOptions<T> baseOptions) {
        boolean isBaseInside;
        if (baseTile == null) {
            return null;
        }
        boolean bl = isBaseInside = !((Entity)mob).getLevel().isOutside(baseTile.x, baseTile.y);
        if (isBaseInside) {
            ConnectedSubRegionsResult regions = null;
            if (baseOptions.isBaseRoom(mob, baseTile)) {
                regions = ((Entity)mob).getLevel().regionManager.getRoomConnectedByTile(baseTile.x, baseTile.y, true, 2000);
            } else if (baseOptions.isBaseHouse(mob, baseTile)) {
                regions = ((Entity)mob).getLevel().regionManager.getHouseConnectedByTile(baseTile.x, baseTile.y, 4000);
            }
            if (regions != null) {
                int maxHouseSize = baseOptions.getBaseRadius(mob, this) * 8;
                return WandererAINode.findWanderingPointInsideRegions(mob, regions, maxHouseSize, tilePriority, 20, 5);
            }
        } else {
            int radius = baseOptions.getBaseRadius(mob, this);
            return WandererAINode.findWanderingPointAround(mob, baseTile.x, baseTile.y, radius, null, (tp, biome) -> {
                if (mob.getDistance(tp.tileX * 32 + 16, tp.tileY * 32 + 16) > (float)(radius * 32)) {
                    return Integer.MIN_VALUE;
                }
                return mob.getTileWanderPriority((TilePosition)tp, (Biome)biome);
            }, 20, 5);
        }
        return null;
    }

    public static Point getWanderingPoint(Mob mob, PriorityMap<Point> priorityMap, int minRandomList, int attempts) {
        if (attempts > minRandomList) {
            throw new IllegalArgumentException("Attempts cannot be larger than minimum number of items in random list");
        }
        ArrayList<Point> positions = priorityMap.getBestObjects(minRandomList);
        while (!positions.isEmpty() && attempts > 0) {
            int index = GameRandom.globalRandom.nextInt(positions.size());
            Point point = positions.get(index);
            if (mob.estimateCanMoveTo(point.x, point.y, false)) {
                return point;
            }
            positions.remove(index);
            --attempts;
        }
        return null;
    }

    public static Point findWanderingPointInsideRegions(Mob mob, ConnectedSubRegionsResult regions, int maxTiles, BiFunction<TilePosition, Biome, Integer> tilePriority, int minRandomList, int regionPathAttempts) {
        Point pathOffset = mob.getPathMoveOffset();
        PriorityMap<Point> priorityMap = new PriorityMap<Point>();
        int added = 0;
        Biome baseBiome = mob.getWanderBaseBiome(mob.getLevel());
        for (SubRegion sr : regions.connectedRegions) {
            if (sr.getType().isSolid) continue;
            for (Point tile : sr.getLevelTiles()) {
                if (mob.getLevel().isSolidTile(tile.x, tile.y) || mob.collidesWith(mob.getLevel(), tile.x * 32 + pathOffset.x, tile.y * 32 + pathOffset.y)) continue;
                priorityMap.add(tilePriority == null ? 0 : tilePriority.apply(new TilePosition(mob.getLevel(), tile), baseBiome), tile);
                if (++added <= maxTiles) continue;
                break;
            }
            if (added <= maxTiles) continue;
            break;
        }
        return WandererAINode.getWanderingPoint(mob, priorityMap, minRandomList, regionPathAttempts);
    }

    public static Point findWanderingPoint(Mob mob, int xOffset, int yOffset, int searchRadius, ZoneTester zoneTester, BiFunction<TilePosition, Biome, Integer> tilePriority, int minRandomList, int regionPathAttempts) {
        return WandererAINode.findWanderingPointAround(mob, mob.getTileX() + xOffset, mob.getTileY() + yOffset, searchRadius, zoneTester, tilePriority, minRandomList, regionPathAttempts);
    }

    public static Point findWanderingPoint(Mob mob, int xOffset, int yOffset, int searchRadius, ZoneTester zoneTester, int minRandomList, int regionPathAttempts) {
        return WandererAINode.findWanderingPointAround(mob, mob.getTileX() + xOffset, mob.getTileY() + yOffset, searchRadius, zoneTester, mob::getTileWanderPriority, minRandomList, regionPathAttempts);
    }

    public static Point findWanderingPointAround(Mob mob, int tileX, int tileY, int searchRadius, ZoneTester zoneTester, BiFunction<TilePosition, Biome, Integer> tilePriority, int minRandomList, int regionPathAttempts) {
        Point pathOffset = mob.getPathMoveOffset();
        PriorityMap<Point> priorityMap = new PriorityMap<Point>();
        Biome baseBiome = mob.getWanderBaseBiome(mob.getLevel());
        for (int x = -searchRadius; x <= searchRadius; ++x) {
            for (int y = -searchRadius; y <= searchRadius; ++y) {
                Point lp = new Point(tileX + x, tileY + y);
                if (zoneTester != null && !zoneTester.containsTile(lp.x, lp.y) || mob.getLevel().isSolidTile(lp.x, lp.y) || mob.collidesWith(mob.getLevel(), lp.x * 32 + pathOffset.x, lp.y * 32 + pathOffset.y)) continue;
                priorityMap.add(tilePriority == null ? 0 : tilePriority.apply(new TilePosition(mob.getLevel(), lp), baseBiome), lp);
            }
        }
        return WandererAINode.getWanderingPoint(mob, priorityMap, minRandomList, regionPathAttempts);
    }

    public static Point findWanderingPointAround(Mob mob, int tileX, int tileY, int searchRadius, ZoneTester zoneTester, int minRandomList, int regionPathAttempts) {
        return WandererAINode.findWanderingPointAround(mob, tileX, tileY, searchRadius, zoneTester, mob::getTileWanderPriority, minRandomList, regionPathAttempts);
    }
}

