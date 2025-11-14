/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.Objects;
import necesse.engine.AreaFinder;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public abstract class TileFinderFlyingAINode<T extends Mob>
extends AINode<T> {
    public long findCooldown = 0L;
    public Point foundTile = null;
    public int maxDistance = 20;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        this.tickFinder(mob, blackboard);
        if (this.foundTile != null) {
            if (!blackboard.mover.isCurrentlyMovingFor(this)) {
                blackboard.mover.setCustomMovement(this, new MobMovementLevelPos(this.foundTile.x * 32 + 16, this.foundTile.y * 32 + 16));
            }
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.FAILURE;
    }

    public void tickFinder(T mob, Blackboard<T> blackboard) {
        long currentTime = mob.getTime();
        if (this.findCooldown > currentTime) {
            return;
        }
        Point lastTile = this.foundTile;
        this.foundTile = null;
        Point startTile = this.getStartTile(mob, blackboard);
        if (startTile == null) {
            return;
        }
        new AreaFinder(startTile.x, startTile.y, this.maxDistance, (Mob)mob, blackboard){
            final /* synthetic */ Mob val$mob;
            final /* synthetic */ Blackboard val$blackboard;
            {
                this.val$mob = mob;
                this.val$blackboard = blackboard;
                super(startX, startY, maxDistance);
            }

            @Override
            public boolean checkPoint(int x, int y) {
                if (TileFinderFlyingAINode.this.isValidTile(this.val$mob, this.val$blackboard, x, y)) {
                    TileFinderFlyingAINode.this.foundTile = new Point(x, y);
                    return true;
                }
                return false;
            }
        }.runFinder();
        if (!Objects.equals(lastTile, this.foundTile) && this.foundTile != null) {
            blackboard.mover.setCustomMovement(this, new MobMovementLevelPos(this.foundTile.x * 32 + 16, this.foundTile.y * 32 + 16));
        }
        this.findCooldown = currentTime + 2000L;
    }

    public abstract boolean isValidTile(T var1, Blackboard<T> var2, int var3, int var4);

    public abstract Point getStartTile(T var1, Blackboard<T> var2);
}

