/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class CritterRunAINode<T extends CritterMob>
extends MoveTaskAINode<T> {
    public boolean runsFromPlayers = true;
    public int runDistance = 150;
    public long nextRunCooldown;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        AINodeResult result;
        TempDistance closest;
        if (((CritterMob)mob).isRunning() && !((Entity)mob).getLevel().isCave && !(((Entity)mob).getLevel() instanceof IncursionLevel) && EscapeAINode.canEscape(mob, 3)) {
            ((Mob)mob).remove();
            return AINodeResult.SUCCESS;
        }
        if (((CritterMob)mob).isRunning() && !blackboard.mover.isMoving()) {
            ((CritterMob)mob).setRunning(false);
        }
        for (AIWasHitEvent e : blackboard.getLastHits()) {
            AINodeResult result2;
            Mob attackOwner;
            Mob mob2 = attackOwner = e.event.attacker != null ? e.event.attacker.getAttackOwner() : null;
            if (!(attackOwner != null ? (result2 = this.startRun(mob, ((Entity)mob).getTileX(), ((Entity)mob).getTileY(), attackOwner)) != null : (result2 = this.startRun(mob, ((Entity)mob).getTileX(), ((Entity)mob).getTileY(), e.event.knockbackX, e.event.knockbackY, (ZoneTester)null)) != null)) continue;
            return result2;
        }
        if (this.runsFromPlayers && this.nextRunCooldown < ((Entity)mob).getWorldEntity().getTime() && (closest = (TempDistance)((Entity)mob).getLevel().entityManager.players.getInRegionByTileRange(((Entity)mob).getTileX(), ((Entity)mob).getTileY(), this.runDistance / 32 + 2).stream().map(p -> new TempDistance((PlayerMob)p, (Mob)mob)).min((p1, p2) -> Float.compare(p1.distance, p2.distance)).orElse(null)) != null && closest.distance <= (float)this.runDistance && (result = this.startRun(mob, closest.player.getTileX(), closest.player.getTileY(), closest.player)) != null) {
            return result;
        }
        return AINodeResult.FAILURE;
    }

    public AINodeResult startRun(T mob, int startTileX, int startTileY, float dx, float dy, ZoneTester zoneTester) {
        int runDistanceTile = (int)Math.ceil((float)this.runDistance / 32.0f);
        Point2D.Float dir = GameMath.normalize(dx, dy);
        float dirMod = Math.abs(dir.x) > Math.abs(dir.y) ? 1.0f / Math.abs(dir.x) : 1.0f / Math.abs(dir.y);
        dir.x *= dirMod;
        dir.y *= dirMod;
        int radius = 8 + runDistanceTile;
        Point runPoint = WandererAINode.findWanderingPointAround(mob, startTileX + (int)(dir.x * (float)radius), startTileY + (int)(dir.y * (float)radius), radius, zoneTester, 20, 5);
        this.nextRunCooldown = ((Entity)mob).getWorldEntity().getTime() + 2000L;
        if (runPoint != null) {
            return this.moveToTileTask(runPoint.x, runPoint.y, null, path -> {
                mob.setRunning(path.moveIfWithin(-1, -1, null));
                return AINodeResult.FAILURE;
            });
        }
        runPoint = WandererAINode.findWanderingPointAround(mob, startTileX, startTileY, radius * 2, zoneTester, 20, 5);
        if (runPoint != null) {
            return this.moveToTileTask(runPoint.x, runPoint.y, null, path -> {
                mob.setRunning(path.moveIfWithin(-1, -1, null));
                return AINodeResult.FAILURE;
            });
        }
        return null;
    }

    public AINodeResult startRun(T mob, int startTileX, int startTileY, float dx, float dy, Mob threat) {
        int runDistanceTile = (int)Math.ceil((float)this.runDistance / 32.0f);
        ZoneTester zoneTester = (tileX, tileY) -> {
            double distance = GameMath.diagonalMoveDistance(tileX, tileY, threat.getTileX(), threat.getTileY());
            return distance >= (double)(runDistanceTile + 2);
        };
        return this.startRun(mob, startTileX, startTileY, dx, dy, zoneTester);
    }

    public AINodeResult startRun(T mob, int startTileX, int startTileY, Mob threat) {
        return this.startRun(mob, startTileX, startTileY, ((CritterMob)mob).x - threat.x, ((CritterMob)mob).y - threat.y, threat);
    }

    protected static class TempDistance {
        public final PlayerMob player;
        public final float distance;

        public TempDistance(PlayerMob player, Mob mob) {
            this.player = player;
            this.distance = player.getDistance(mob);
        }
    }
}

