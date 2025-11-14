/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.path;

import java.awt.Shape;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class PathOptions {
    public final TilePathfinding.NodePriority nodePriority;

    public PathOptions(TilePathfinding.NodePriority nodePriority) {
        this.nodePriority = nodePriority;
    }

    public PathOptions() {
        this(TilePathfinding.NodePriority.TOTAL_COST);
    }

    public boolean canPassTile(TickManager tickManager, Level level, Mob mob, PathDoorOption doorOption, CollisionFilter levelCollisionFilter, int tileX, int tileY) {
        return doorOption.canPass(tileX, tileY);
    }

    public boolean checkCanPassDoorOrTile(TickManager tickManager, Level level, Mob mob, PathDoorOption doorOption, CollisionFilter levelCollisionFilter, int tileX, int tileY) {
        GameObject object = level.getObject(tileX, tileY);
        if (object.isDoor && doorOption.canPassDoor((DoorObject)object, tileX, tileY)) {
            return true;
        }
        return doorOption.canBreakDown(tileX, tileY) || this.canPassTile(tickManager, level, mob, doorOption, levelCollisionFilter, tileX, tileY);
    }

    public double getTileCost(Level level, Mob mob, PathDoorOption doorOption, int tileX, int tileY) {
        double mod = level.getTile(tileX, tileY).getPathCost(level, tileX, tileY, mob);
        GameObject object = level.getObject(tileX, tileY);
        mod += object.getPathCost(level, tileX, tileY);
        if (doorOption == null) {
            return mod;
        }
        if (object.isDoor && (((DoorObject)object).isOpen(level, tileX, tileY, level.getObjectRotation(tileX, tileY)) || doorOption.canOpen(tileX, tileY) && !((DoorObject)object).isForceClosed(level, tileX, tileY))) {
            return mod;
        }
        if (object.isSolid(level, tileX, tileY) && doorOption.canBreakDown(tileX, tileY)) {
            mod += object.getBreakDownPathCost(level, tileX, tileY);
        }
        return mod;
    }

    public boolean canMoveLine(TickManager tickManager, Level level, Mob mob, CollisionFilter levelCollisionFilter, int x1, int y1, int x2, int y2) {
        return Performance.record((PerformanceTimerManager)tickManager, "canPassLine", () -> !level.collides((Shape)new MovedRectangle(mob, x1, y1, x2, y2), levelCollisionFilter));
    }
}

