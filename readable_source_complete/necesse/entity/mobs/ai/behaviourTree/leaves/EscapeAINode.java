/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Comparator;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public abstract class EscapeAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public long nextPathFind;
    public Point2D.Float escapeDirection;

    public abstract boolean shouldEscape(T var1, Blackboard<T> var2);

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    public void onEscaped(T mob) {
        ((Mob)mob).remove();
    }

    public static boolean canEscape(Mob mob, int edgePadding) {
        Level level = mob.getLevel();
        int mobTileX = mob.getTileX();
        int mobTileY = mob.getTileY();
        if (level.tileWidth > 0) {
            return mobTileX < edgePadding || mobTileX >= level.tileWidth - edgePadding - 1;
        }
        if (level.tileHeight > 0) {
            return mobTileY < edgePadding || mobTileY >= level.tileHeight - edgePadding - 1;
        }
        Region region = level.regionManager.getRegionByTile(mobTileX, mobTileY, false);
        if (region == null) {
            return true;
        }
        int tileXWithinRegion = mobTileX - region.tileXOffset;
        int tileYWithinRegion = mobTileY - region.tileYOffset;
        if (tileXWithinRegion <= edgePadding && !EscapeAINode.isRegionLoaded(level, region.regionX - 1, region.regionY)) {
            return true;
        }
        if (tileXWithinRegion >= region.tileWidth - 1 - edgePadding && !EscapeAINode.isRegionLoaded(level, region.regionX + 1, region.regionY)) {
            return true;
        }
        if (tileYWithinRegion <= edgePadding && !EscapeAINode.isRegionLoaded(level, region.regionX, region.regionY - 1)) {
            return true;
        }
        return tileYWithinRegion >= region.tileHeight - 1 - edgePadding && !EscapeAINode.isRegionLoaded(level, region.regionX, region.regionY + 1);
    }

    private static boolean isRegionLoaded(Level level, int regionX, int regionY) {
        return level.regionManager.isRegionLoaded(regionX, regionY);
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        if (this.shouldEscape(mob, blackboard)) {
            if (EscapeAINode.canEscape(mob, 4)) {
                this.onEscaped(mob);
                return AINodeResult.SUCCESS;
            }
            int mobTileX = ((Entity)mob).getTileX();
            int mobTileY = ((Entity)mob).getTileY();
            if (this.escapeDirection == null) {
                ServerClient closest = GameUtils.streamServerClients(((Entity)mob).getLevel()).min(Comparator.comparing(c -> Float.valueOf(mob.getDistance(c.playerMob)))).orElse(null);
                if (closest != null) {
                    this.escapeDirection = GameMath.normalize(((Mob)mob).x - closest.playerMob.x, ((Mob)mob).y - closest.playerMob.y);
                }
                if (this.escapeDirection == null) {
                    this.escapeDirection = GameMath.getAngleDir(GameRandom.globalRandom.nextInt(360));
                }
            }
            if (this.nextPathFind < mob.getLocalTime() || !blackboard.mover.isCurrentlyMovingFor(this)) {
                this.nextPathFind = mob.getLocalTime() + 1000L;
                for (int i = 32; i > 1; --i) {
                    Point tile = new Point((int)((float)mobTileX + this.escapeDirection.x * (float)i), (int)((float)mobTileY + this.escapeDirection.y * (float)i));
                    if (!((Mob)mob).estimateCanMoveTo(tile.x, tile.y, true)) continue;
                    return this.moveToTileTask(tile.x, tile.y, null, path -> {
                        if (path.moveIfWithin(-1, -1, () -> {
                            this.nextPathFind = 0L;
                        })) {
                            int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 2000, 0.1f);
                            this.nextPathFind = mob.getLocalTime() + (long)nextPathTimeAdd;
                        }
                        return AINodeResult.SUCCESS;
                    });
                }
                float escapeAngle = GameMath.getAngle(this.escapeDirection);
                this.escapeDirection = GameMath.getAngleDir(GameRandom.globalRandom.getFloatOffset(escapeAngle, 90.0f));
            }
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.FAILURE;
    }
}

