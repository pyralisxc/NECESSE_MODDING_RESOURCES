/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.PollinateObject;
import necesse.level.gameObject.PollinateObjectHandler;

public class BeePollinateAINode<T extends HoneyBeeMob>
extends MoveTaskAINode<T> {
    public PollinateObjectHandler target;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.target != null) {
            this.target.reservable.reserve((Entity)mob);
        }
        return super.tick(mob, blackboard);
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        if (((HoneyBeeMob)mob).pollinateTime == 0L) {
            return AINodeResult.FAILURE;
        }
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            return AINodeResult.SUCCESS;
        }
        if (this.target == null && ((HoneyBeeMob)mob).pollinateTime <= mob.getTime()) {
            Point baseTile = ((HoneyBeeMob)mob).apiaryHome != null ? ((HoneyBeeMob)mob).apiaryHome : new Point(((Entity)mob).getTileX(), ((Entity)mob).getTileY());
            GameTileRange range = AbstractBeeHiveObjectEntity.pollinateTileRange;
            ArrayList<PollinateObjectHandler> validTiles = new ArrayList<PollinateObjectHandler>();
            Point pathOffset = ((Mob)mob).getPathMoveOffset();
            for (Point tile : range.getValidTiles(baseTile.x, baseTile.y)) {
                PollinateObjectHandler handler;
                GameObject object = ((Entity)mob).getLevel().getObject(tile.x, tile.y);
                if (((Entity)mob).getLevel().isSolidTile(tile.x, tile.y) || !object.isSeed || ((Mob)mob).collidesWith(((Entity)mob).getLevel(), tile.x * 32 + pathOffset.x, tile.y * 32 + pathOffset.y) || !((Mob)mob).estimateCanMoveTo(tile.x, tile.y, false) || !(object instanceof PollinateObject) || (handler = ((PollinateObject)((Object)object)).getPollinateHandler(((Entity)mob).getLevel(), tile.x, tile.y)) == null || !handler.canPollinate() || !handler.reservable.isAvailable((Entity)mob)) continue;
                validTiles.add(handler);
            }
            if (!validTiles.isEmpty()) {
                this.target = (PollinateObjectHandler)GameRandom.globalRandom.getOneOf(validTiles);
                this.target.reservable.reserve((Entity)mob);
                return this.moveToTileTask(this.target.tileX, this.target.tileY, null, path -> {
                    if (path.moveIfWithin(-1, 0, null)) {
                        return AINodeResult.SUCCESS;
                    }
                    return AINodeResult.FAILURE;
                });
            }
        }
        if (this.target != null && this.target.isValid()) {
            if (this.target.canPollinate() && !((HoneyBeeMob)mob).isPollinating() && GameMath.diagonalMoveDistance(this.target.tileX, this.target.tileY, ((Entity)mob).getTileX(), ((Entity)mob).getTileY()) <= 1.0) {
                ((HoneyBeeMob)mob).pollinateAbility.runAndSend(this.target, 10000);
            }
            if (((HoneyBeeMob)mob).isPollinating()) {
                return AINodeResult.SUCCESS;
            }
            this.target = null;
            ((HoneyBeeMob)mob).pollinateTime = 0L;
            return AINodeResult.SUCCESS;
        }
        this.target = null;
        return AINodeResult.FAILURE;
    }
}

