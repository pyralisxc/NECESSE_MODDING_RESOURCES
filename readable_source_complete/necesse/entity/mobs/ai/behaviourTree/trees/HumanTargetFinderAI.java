/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import java.util.function.BiPredicate;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.pathfinding.PathResult;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanAngerTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanCommandAttackTargetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetValidity;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HumanTargetFinderAI<T extends HumanMob>
extends SelectorAINode<T> {
    public boolean attackHostiles;
    public boolean ignoreHiding;

    public HumanTargetFinderAI(int searchDistance, boolean attackHostiles, boolean ignoreHiding) {
        this.attackHostiles = attackHostiles;
        this.ignoreHiding = ignoreHiding;
        TargetFinderDistance distance = new TargetFinderDistance<T>(searchDistance){

            @Override
            protected int getSearchDistanceFlat(T mob, Mob target) {
                NetworkSettlementData settlement;
                boolean homeOutside;
                int addedDistance = 0;
                boolean bl = homeOutside = ((HumanMob)mob).hasCommandOrders() || ((HumanMob)mob).home == null || ((Entity)mob).getLevel().isOutside(((HumanMob)mob).home.x, ((HumanMob)mob).home.y);
                if (!homeOutside) {
                    addedDistance = 160;
                }
                if ((settlement = ((HumanMob)mob).getSettlerSettlementNetworkData()) != null && settlement.isRaidActive()) {
                    addedDistance = 320;
                }
                return super.getSearchDistanceFlat(mob, target) + addedDistance;
            }
        };
        distance.targetLostAddedDistance = 160;
        TargetValidity validity = new TargetValidity<T>(){

            @Override
            public boolean isValidTarget(AINode<T> node, T mob, Mob target, boolean isNewTarget) {
                if (!super.isValidTarget(node, mob, target, isNewTarget)) {
                    return false;
                }
                boolean homeOutside = !mob.isSettlerOnCurrentLevel() || ((HumanMob)mob).hasCommandOrders() && !((HumanMob)mob).isHiding || ((HumanMob)mob).home == null || ((Entity)mob).getLevel().isOutside(((HumanMob)mob).home.x, ((HumanMob)mob).home.y);
                int roomID = ((HumanMob)mob).home == null ? 0 : ((Entity)mob).getLevel().getRoomID(((HumanMob)mob).home.x, ((HumanMob)mob).home.y);
                Boolean isHidingInside = HumanTargetFinderAI.this.getBlackboard().getObject(Boolean.class, "isHidingInside");
                ComputedValue<ZoneTester> zoneTester = new ComputedValue<ZoneTester>(() -> {
                    if (mob.levelSettler != null && !mob.hasCommandOrders()) {
                        return mob.levelSettler.isTileInSettlementBoundsAndRestrictZoneTester();
                    }
                    return (x, y) -> true;
                });
                return HumanTargetFinderAI.this.isValidTarget(mob, target, homeOutside, roomID, isHidingInside != null && isHidingInside != false, zoneTester, isNewTarget);
            }
        };
        this.addChild(new HumanAngerTargetAINode(distance, validity));
        this.addChild(new HumanCommandAttackTargetterAINode());
        TargetFinderAINode targetFinder = new TargetFinderAINode<T>(distance, validity){

            @Override
            public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                return HumanTargetFinderAI.this.streamHumanAITargets(mob, distance);
            }
        };
        targetFinder.moveToAttacker = false;
        targetFinder.runOnGlobalTick = true;
        targetFinder.loseTargetMinCooldown = 1000;
        targetFinder.loseTargetMaxCooldown = 3000;
        targetFinder.noTargetFoundMinCooldown = 750;
        targetFinder.noTargetFoundMaxCooldown = 1250;
        this.addChild(targetFinder);
    }

    protected boolean isTargetSameRoom(Mob target, int roomID) {
        int tileX = target.getTileX();
        int tileY = target.getTileY();
        if (target.getLevel().getRoomID(tileX, tileY) == roomID) {
            return true;
        }
        return target.getLevel().regionManager.getSubRegionByTile(tileX, tileY).streamAdjacentRegions().anyMatch(r -> r.getRoomID() == roomID);
    }

    public boolean isValidTarget(T mob, Mob target, boolean homeOutside, int homeRoomID, boolean isHidingInside, ComputedValue<ZoneTester> zoneTester, boolean isNewTarget) {
        if (!homeOutside) {
            NetworkSettlementData settlement;
            if (this.isTargetSameRoom(target, homeRoomID)) {
                return true;
            }
            if (((HumanMob)mob).isHiding) {
                return false;
            }
            if (!this.ignoreHiding && isHidingInside && (settlement = ((HumanMob)mob).getSettlerSettlementNetworkData()) != null && !settlement.isRaidActive()) {
                return false;
            }
            if (((HumanMob)mob).home == null || GameMath.squareDistance(((HumanMob)mob).home.x, ((HumanMob)mob).home.y, target.getTileX(), target.getTileY()) <= 20.0f) {
                ZoneTester zone = zoneTester.get();
                return zone == null || zone.containsTile(target.getTileX(), target.getTileY());
            }
            return false;
        }
        if (((HumanMob)mob).hasCommandOrders()) {
            int maxDistance;
            Point baseTile = ((HumanMob)mob).commandFollowMob != null && !((HumanMob)mob).commandFollowMob.removed() && ((Entity)mob).isSamePlace(((HumanMob)mob).commandFollowMob) ? new Point(((HumanMob)mob).commandFollowMob.getTileX(), ((HumanMob)mob).commandFollowMob.getTileY()) : (((HumanMob)mob).commandGuardPoint != null ? new Point(GameMath.getTileCoordinate(((HumanMob)mob).commandGuardPoint.x), GameMath.getTileCoordinate(((HumanMob)mob).commandGuardPoint.y)) : new Point(((Entity)mob).getTileX(), ((Entity)mob).getTileY()));
            int n = maxDistance = ((HumanMob)mob).isHiding ? 10 : 20;
            if (!isNewTarget) {
                maxDistance += 10;
            }
            if (GameMath.squareDistance(baseTile.x, baseTile.y, target.getTileX(), target.getTileY()) <= (float)maxDistance) {
                if (!isNewTarget) {
                    return true;
                }
                int finalMaxDistance = maxDistance;
                return Performance.record((PerformanceTimerManager)((Entity)mob).getLevel().tickManager(), "settlerGuardTarget", () -> {
                    BiPredicate<Point, Point> isAtTarget = mob.canBeTargetedFromAdjacentTiles() ? TilePathfinding.isAtOrAdjacentObject(target.getLevel(), target.getTileX(), target.getTileY()) : null;
                    TilePathfinding tilePathFinding = new TilePathfinding(mob.getLevel().tickManager(), mob.getLevel(), (Mob)mob, isAtTarget, this.getBlackboard().mover.getPathOptions(this));
                    PathResult result = tilePathFinding.findPath(baseTile, new Point(target.getTileX(), target.getTileY()), finalMaxDistance + 5);
                    return result.foundTarget;
                });
            }
            return false;
        }
        return ((HumanMob)mob).home == null || GameMath.squareDistance(((HumanMob)mob).home.x, ((HumanMob)mob).home.y, target.getTileX(), target.getTileY()) <= 20.0f;
    }

    public GameAreaStream<Mob> streamHumanAITargets(T mob, TargetFinderDistance<T> distance) {
        if (((HumanMob)mob).commandFollowMob != null && ((HumanMob)mob).commandMoveToFollowPoint) {
            return GameAreaStream.empty();
        }
        if (((HumanMob)mob).isVisitor()) {
            return GameAreaStream.empty();
        }
        boolean homeOutside = ((HumanMob)mob).hasCommandOrders() || ((HumanMob)mob).home == null || ((Entity)mob).getLevel().isOutside(((HumanMob)mob).home.x, ((HumanMob)mob).home.y);
        Point base = new Point(((Entity)mob).getX(), ((Entity)mob).getY());
        if (!homeOutside) {
            base = new Point(((HumanMob)mob).home.x * 32 + 16, ((HumanMob)mob).home.y * 32 + 16);
        }
        Point finalBase = base;
        return distance.streamMobsAndPlayersInRange(base, mob).filter(m -> m.canTakeDamage() && m.canBeHit((Attacker)mob)).filter(((HumanMob)mob).filterHumanTargets()).filter(target -> target.getDistance(finalBase.x, finalBase.y) < (float)distance.getSearchDistance(mob, (Mob)target));
    }
}

