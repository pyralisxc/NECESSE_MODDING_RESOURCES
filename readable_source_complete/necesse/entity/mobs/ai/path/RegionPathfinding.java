/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.path;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.HashProxyLinkedList;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.ai.path.SubRegionPathResult;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SubRegion;

public class RegionPathfinding
extends Pathfinding<SubRegion> {
    public PathDoorOption doorOption;
    public BiPredicate<SubRegion, SubRegion> isAtTarget;

    public static boolean canMoveToTile(Level level, int fromTileX, int fromTileY, int toTileX, int toTileY, PathDoorOption doorOption, boolean acceptAdjacentTiles) {
        return RegionPathfinding.canMoveToTile(level, fromTileX, fromTileY, toTileX, toTileY, doorOption, acceptAdjacentTiles, 200);
    }

    public static boolean canMoveToTile(Level level, int fromTileX, int fromTileY, int toTileX, int toTileY, PathDoorOption doorOption, boolean acceptAdjacentTiles, int maxIterations) {
        int fromRegionID = level.regionManager.getRegionIDByTile(fromTileX, fromTileY);
        if (fromRegionID == 0) {
            return false;
        }
        int toRegionID = level.regionManager.getRegionIDByTile(toTileX, toTileY);
        if (toRegionID == 0) {
            return false;
        }
        if (fromRegionID == toRegionID) {
            return true;
        }
        if (!acceptAdjacentTiles) {
            BiPredicate<SubRegion, SubRegion> isAtTarget = (current, target) -> current.getRegionID() == target.getRegionID();
            return RegionPathfinding.findMoveToTile((Level)level, (int)fromTileX, (int)fromTileY, (int)toTileX, (int)toTileY, (PathDoorOption)doorOption, isAtTarget, (int)maxIterations).foundTarget;
        }
        HashSet<SubRegion> accepted = new HashSet<SubRegion>();
        accepted.add(level.regionManager.getSubRegionByTile(toTileX, toTileY));
        for (Point tile : level.getObject(toTileX, toTileY).getMultiTile(level, 0, toTileX, toTileY).getAdjacentTiles(toTileX, toTileY, true)) {
            if (level.regionManager.getRegionIDByTile(tile.x, tile.y) == fromRegionID) {
                return true;
            }
            accepted.add(level.regionManager.getSubRegionByTile(tile.x, tile.y));
        }
        BiPredicate<SubRegion, SubRegion> isAtTarget = (current, target) -> current.getRegionID() == target.getRegionID() || accepted.contains(current);
        return RegionPathfinding.findMoveToTile((Level)level, (int)fromTileX, (int)fromTileY, (int)toTileX, (int)toTileY, (PathDoorOption)doorOption, isAtTarget, (int)maxIterations).foundTarget;
    }

    public static PathResult<SubRegion, RegionPathfinding> findMoveToTile(Level level, int fromTileX, int fromTileY, int toTileX, int toTileY, PathDoorOption doorOption, BiPredicate<SubRegion, SubRegion> isAtTarget) {
        return RegionPathfinding.findMoveToTile(level, fromTileX, fromTileY, toTileX, toTileY, doorOption, isAtTarget, 200);
    }

    public static PathResult<SubRegion, RegionPathfinding> findMoveToTile(Level level, int fromTileX, int fromTileY, int toTileX, int toTileY, PathDoorOption doorOption, BiPredicate<SubRegion, SubRegion> isAtTarget, int maxIterations) {
        RegionPathfinding regionPath = new RegionPathfinding(doorOption, isAtTarget);
        SubRegion from = level.regionManager.getSubRegionByTile(fromTileX, fromTileY);
        SubRegion to = level.regionManager.getSubRegionByTile(toTileX, toTileY);
        return regionPath.findPath(from, to, maxIterations);
    }

    public RegionPathfinding(PathDoorOption doorOption, BiPredicate<SubRegion, SubRegion> isAtTarget) {
        this.doorOption = doorOption;
        this.isAtTarget = isAtTarget;
    }

    @Override
    public <C extends Pathfinding<SubRegion>> PathResult<SubRegion, C> findPath(SubRegion from, SubRegion target, int maxIterations) {
        if (from == null || target == null) {
            return super.findPath(from, target, maxIterations);
        }
        return Performance.record((PerformanceTimerManager)from.region.manager.level.tickManager(), "regionPathfinding", () -> super.findPath(from, target, maxIterations));
    }

    @Override
    protected boolean isAtTarget(SubRegion current, SubRegion target) {
        if (this.isAtTarget == null) {
            return current == target;
        }
        return this.isAtTarget.test(current, target);
    }

    @Override
    protected void handleConnectedNodes(Pathfinding.Node from, HashProxyLinkedList<Pathfinding.Node, SubRegion> openNodes, HashProxyLinkedList<Pathfinding.Node, SubRegion> closedNodes, HashSet<SubRegion> invalidChecked, Function<SubRegion, Boolean> handler, BiConsumer<Pathfinding.Node, Pathfinding.Node> connectedHandler, Runnable interrupt) {
        for (SubRegion sr : ((SubRegion)from.item).getAdjacentRegions()) {
            Pathfinding.Node openNode = openNodes.getObject(sr);
            if (openNode != null) {
                if (openNode.reverseDirection == from.reverseDirection) continue;
                connectedHandler.accept(openNode, from);
                return;
            }
            Pathfinding.Node closedNode = closedNodes.getObject(sr);
            if (closedNode != null) {
                if (closedNode.reverseDirection == from.reverseDirection) continue;
                connectedHandler.accept(closedNode, from);
                return;
            }
            SubRegionPathResult pathThrough = this.doorOption.canPathThrough(sr);
            boolean valid = pathThrough == SubRegionPathResult.VALID ? true : (pathThrough == SubRegionPathResult.CHECK_EACH_TILE ? sr.streamLevelTiles().anyMatch(tile -> this.doorOption.canPathThroughCheckTile(sr, tile.x, tile.y)) : false);
            if (valid) {
                if (!handler.apply(sr).booleanValue()) continue;
                return;
            }
            invalidChecked.add(sr);
        }
    }

    @Override
    protected double getNodeHeuristicCost(SubRegion currentNode, SubRegion targetNode) {
        return GameMath.diagonalMoveDistance(currentNode.region.regionX, currentNode.region.regionY, targetNode.region.regionX, targetNode.region.regionY);
    }

    @Override
    protected double getNodeCost(SubRegion node) {
        return 0.0;
    }

    @Override
    protected double getNodePathCost(SubRegion node, SubRegion cameFrom) {
        return 1.0;
    }

    public static double estimatePathTileLength(LinkedList<Pathfinding.Node> path) {
        double length = 0.0;
        Pathfinding.Node last = null;
        for (Pathfinding.Node current : path) {
            if (last != null) {
                length += ((SubRegion)last.item).getAverageLevelTile().distance(((SubRegion)current.item).getAverageLevelTile());
            }
            last = current;
        }
        return length;
    }
}

