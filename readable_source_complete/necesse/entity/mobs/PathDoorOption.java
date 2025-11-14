/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.mobs.ai.path.RegionPathfinding;
import necesse.entity.mobs.ai.path.SubRegionPathResult;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionType;
import necesse.level.maps.regionSystem.SubRegion;
import necesse.level.maps.regionSystem.SubRegionEventListener;

public abstract class PathDoorOption {
    public final String debugName;
    public final Level level;
    public int maxIterations = 10000;
    private final HashMap<Integer, HashMap<Integer, Cache>> canRegionPathTo = new HashMap();

    public PathDoorOption(String debugName, Level level) {
        this.debugName = debugName;
        this.level = level;
    }

    public SubRegionPathResult canPathThrough(SubRegion subregion) {
        if (subregion.getType().isDoor) {
            return SubRegionPathResult.CHECK_EACH_TILE;
        }
        if (subregion.getType().isSolid) {
            return SubRegionPathResult.INVALID;
        }
        return SubRegionPathResult.VALID;
    }

    public boolean canPathThroughCheckTile(SubRegion subregion, int tileX, int tileY) {
        if (this.canBreakDown(tileX, tileY)) {
            return true;
        }
        GameObject object = this.level.getObject(tileX, tileY);
        if (object.isDoor) {
            return ((DoorObject)object).isOpen(this.level, tileX, tileY, this.level.getObjectRotation(tileX, tileY)) || this.canOpen(tileX, tileY) && !((DoorObject)object).isForceClosed(this.level, tileX, tileY);
        }
        return false;
    }

    public boolean canPass(int tileX, int tileY) {
        return this.level.regionManager.getRegionTypeByTile(tileX, tileY) == RegionType.OPEN;
    }

    public boolean canPassDoor(DoorObject doorObject, int tileX, int tileY) {
        return doorObject.isOpen(this.level, tileX, tileY, this.level.getObjectRotation(tileX, tileY)) || this.canOpen(tileX, tileY) && !doorObject.isForceClosed(this.level, tileX, tileY);
    }

    public abstract boolean canBreakDown(int var1, int var2);

    public abstract boolean canOpen(int var1, int var2);

    public abstract boolean canClose(int var1, int var2);

    public abstract boolean doorChangeInvalidatesCache(DoorObject var1, DoorObject var2, int var3, int var4);

    public int getTotalCachedPaths() {
        return this.canRegionPathTo.values().stream().mapToInt(HashMap::size).sum();
    }

    public Collection<Integer> getSourcePathRegionIDs() {
        return this.canRegionPathTo.keySet();
    }

    public Collection<Integer> getDestinationPathRegionIDs(int sourceRegionID) {
        HashMap<Integer, Cache> map = this.canRegionPathTo.get(sourceRegionID);
        if (map == null) {
            return new LinkedList<Integer>();
        }
        return map.keySet();
    }

    public Collection<SubRegion> getCachedPath(int sourceRegionID, int destinationRegionID) {
        HashMap<Integer, Cache> map = this.canRegionPathTo.get(sourceRegionID);
        if (map == null) {
            return new LinkedList<SubRegion>();
        }
        Cache cache = map.get(destinationRegionID);
        if (cache == null) {
            return new LinkedList<SubRegion>();
        }
        return cache.path;
    }

    public void invalidateCache() {
        this.canRegionPathTo.forEach((n1, map) -> map.forEach((n2, cache) -> cache.invalidateListeners()));
        this.canRegionPathTo.clear();
    }

    private HashMap<Integer, Cache> getFromRegionID(int regionID) {
        return this.canRegionPathTo.compute(regionID, (i, last) -> last == null ? new HashMap() : last);
    }

    private void onCacheInvalidated(int fromRegionID, int toRegionID) {
        HashMap<Integer, Cache> pathFromCache = this.canRegionPathTo.get(fromRegionID);
        if (pathFromCache != null) {
            pathFromCache.remove(toRegionID);
            if (pathFromCache.isEmpty()) {
                this.canRegionPathTo.remove(fromRegionID);
            }
        }
    }

    private void cacheFoundPath(PathResult<SubRegion, RegionPathfinding> result, int fromRegionID, int toRegionID) {
        if (result.foundTarget) {
            HashMap regionIDSemiRegion = new HashMap();
            for (int i = 0; i < result.path.size(); ++i) {
                SubRegion sr = (SubRegion)result.path.get((int)i).item;
                regionIDSemiRegion.put(sr.getRegionID(), result.path.subList(i, result.path.size()));
            }
            Iterator i = regionIDSemiRegion.keySet().iterator();
            while (i.hasNext()) {
                HashMap<Integer, Cache> pathFromCache;
                Cache toCache;
                int regionID = (Integer)i.next();
                if (regionID == toRegionID || (toCache = (pathFromCache = this.getFromRegionID(regionID)).get(toRegionID)) != null) continue;
                List nodes = (List)regionIDSemiRegion.get(regionID);
                pathFromCache.put(toRegionID, new Cache(true, nodes.stream().map(n -> (SubRegion)n.item), () -> this.onCacheInvalidated(regionID, toRegionID)));
            }
        } else {
            HashSet<Integer> pathRegionIDs = new HashSet<Integer>();
            for (Pathfinding.Node node : result.path) {
                pathRegionIDs.add(((SubRegion)node.item).getRegionID());
            }
            HashSet<SubRegion> pathSemiRegions = new HashSet<SubRegion>();
            HashSet<SubRegion> adjacentSemiRegions = new HashSet<SubRegion>();
            for (SubRegion sr : GameUtils.mapIterable(GameUtils.concatIterators(result.closedNodes.iterator(), result.openNodes.iterator()), n -> (SubRegion)n.item)) {
                pathSemiRegions.add(sr);
                adjacentSemiRegions.remove(sr);
                for (SubRegion adj : sr.getAdjacentRegions()) {
                    if (pathSemiRegions.contains(adj)) continue;
                    adjacentSemiRegions.add(adj);
                }
            }
            Iterator<SubRegion> iterator = pathRegionIDs.iterator();
            while (iterator.hasNext()) {
                HashMap<Integer, Cache> pathFromCache;
                Cache toCache;
                int regionID = (Integer)((Object)iterator.next());
                if (regionID == toRegionID || (toCache = (pathFromCache = this.getFromRegionID(regionID)).get(toRegionID)) != null) continue;
                pathFromCache.put(toRegionID, new Cache(false, adjacentSemiRegions.stream(), () -> this.onCacheInvalidated(regionID, toRegionID)));
            }
        }
    }

    public boolean canMoveToTile(int fromTileX, int fromTileY, int toTileX, int toTileY, boolean acceptAdjacentTiles) {
        int fromRegionID = this.level.regionManager.getRegionIDByTile(fromTileX, fromTileY);
        if (fromRegionID == 0) {
            return false;
        }
        int toRegionID = this.level.regionManager.getRegionIDByTile(toTileX, toTileY);
        if (toRegionID == 0) {
            return false;
        }
        if (fromRegionID == toRegionID) {
            return true;
        }
        HashMap<Integer, Cache> fromCache = this.getFromRegionID(fromRegionID);
        Cache toCache = fromCache.get(toRegionID);
        if (toCache != null && toCache.canPath) {
            return true;
        }
        if (!acceptAdjacentTiles) {
            if (toCache != null) {
                return toCache.canPath;
            }
            BiPredicate<SubRegion, SubRegion> isAtTarget = (current, target) -> current.getRegionID() == target.getRegionID();
            PathResult<SubRegion, RegionPathfinding> result = RegionPathfinding.findMoveToTile(this.level, fromTileX, fromTileY, toTileX, toTileY, this, isAtTarget, this.maxIterations);
            this.cacheFoundPath(result, fromRegionID, toRegionID);
            return result.foundTarget;
        }
        HashSet<Integer> accepted = new HashSet<Integer>();
        accepted.add(toRegionID);
        boolean foundAllCaches = true;
        for (Point tile : this.level.getObject(toTileX, toTileY).getMultiTile(this.level, 0, toTileX, toTileY).getAdjacentTiles(toTileX, toTileY, true)) {
            int adjacentRegionID = this.level.regionManager.getRegionIDByTile(tile.x, tile.y);
            if (adjacentRegionID == fromRegionID) {
                return true;
            }
            Cache adjacentCache = fromCache.get(adjacentRegionID);
            if (adjacentCache != null) {
                if (adjacentCache.canPath) {
                    return true;
                }
            } else {
                foundAllCaches = false;
            }
            accepted.add(adjacentRegionID);
        }
        if (foundAllCaches) {
            return false;
        }
        BiPredicate<SubRegion, SubRegion> isAtTarget = (current, target) -> current.getRegionID() == target.getRegionID() || accepted.contains(current.getRegionID());
        PathResult<SubRegion, RegionPathfinding> result = RegionPathfinding.findMoveToTile(this.level, fromTileX, fromTileY, toTileX, toTileY, this, isAtTarget, this.maxIterations);
        if (result.foundTarget) {
            toRegionID = ((SubRegion)result.path.get((int)(result.path.size() - 1)).item).getRegionID();
        }
        this.cacheFoundPath(result, fromRegionID, toRegionID);
        return result.foundTarget;
    }

    private class Cache {
        private final boolean canPath;
        private LinkedList<SubRegion> path;
        private final LinkedList<SubRegionEventListener> invalidHandlers = new LinkedList();

        public Cache(boolean canPath, Stream<SubRegion> watchRegions, Runnable onInvalidated) {
            this.canPath = canPath;
            if (GlobalData.isDevMode()) {
                this.path = new LinkedList();
            }
            ((Stream)watchRegions.sequential()).forEach(sr -> {
                if (GlobalData.isDevMode()) {
                    this.path.add((SubRegion)sr);
                }
                SubRegionEventListener listener = sr.addListener(() -> {
                    this.invalidateListeners();
                    onInvalidated.run();
                }, (lastDoor, newDoor, tileX, tileY) -> {
                    if (PathDoorOption.this.doorChangeInvalidatesCache(lastDoor, newDoor, tileX, tileY)) {
                        this.invalidateListeners();
                        onInvalidated.run();
                    }
                });
                this.invalidHandlers.add(listener);
            });
        }

        public void invalidateListeners() {
            for (SubRegionEventListener listener : this.invalidHandlers) {
                listener.submitHandlerInvalidated();
            }
            this.invalidHandlers.clear();
            if (this.path != null) {
                this.path.clear();
            }
        }
    }
}

