/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.AINodeResult
 *  necesse.entity.mobs.ai.behaviourTree.Blackboard
 *  necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode
 *  necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent
 *  necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode
 *  necesse.level.maps.levelData.settlementData.ZoneTester
 */
package aphorea.mobs.ai;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.function.Predicate;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class AphRunFromMobsAI<T extends Mob>
extends MoveTaskAINode<T> {
    public int runDistance;
    public Predicate<Mob> runFromMob;
    public long nextRunCooldown;
    public boolean isRunning;

    public AphRunFromMobsAI() {
        this.runDistance = 150;
        this.runFromMob = mob -> true;
    }

    public AphRunFromMobsAI(String runFromMobID) {
        this.runDistance = 150;
        this.runFromMob = mob -> Objects.equals(mob.getStringID(), runFromMobID);
    }

    public AphRunFromMobsAI(Predicate<Mob> runFromMob) {
        this.runDistance = 150;
        this.runFromMob = runFromMob;
    }

    public AphRunFromMobsAI(int runDistance) {
        this.runDistance = runDistance;
        this.runFromMob = mob -> true;
    }

    public AphRunFromMobsAI(int runDistance, String runFromMobID) {
        this.runDistance = runDistance;
        this.runFromMob = mob -> Objects.equals(mob.getStringID(), runFromMobID);
    }

    public AphRunFromMobsAI(int runDistance, Predicate<Mob> runFromMob) {
        this.runDistance = runDistance;
        this.runFromMob = runFromMob;
    }

    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    public void init(T mob, Blackboard<T> blackboard) {
    }

    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        AINodeResult result;
        TempDistance closest;
        if (this.isRunning && !mob.getLevel().isCave && !new Rectangle(96, 96, (mob.getLevel().tileWidth - 6) * 32, (mob.getLevel().tileHeight - 6) * 32).contains(mob.getCollision())) {
            mob.remove();
            return AINodeResult.SUCCESS;
        }
        if (this.isRunning && !blackboard.mover.isMoving()) {
            this.setRunning((Mob)mob, false);
        }
        for (AIWasHitEvent e : blackboard.getLastHits()) {
            Mob attackOwner = e.event.attacker != null ? e.event.attacker.getAttackOwner() : null;
            AINodeResult result2 = attackOwner != null ? this.startRun(mob, mob.getTileX(), mob.getTileY(), attackOwner) : this.startRun(mob, mob.getTileX(), mob.getTileY(), e.event.knockbackX, e.event.knockbackY, (ZoneTester)null);
            if (result2 == null) continue;
            return this.alwaysReturnSuccess() ? AINodeResult.SUCCESS : result2;
        }
        if (this.nextRunCooldown < mob.getWorldEntity().getTime() && (closest = (TempDistance)mob.getLevel().entityManager.streamAreaMobsAndPlayers(((Mob)mob).x, ((Mob)mob).y, this.runDistance).filter(this.runFromMob).map(p -> new TempDistance((Mob)p, (Mob)mob)).findBestDistance(0, (p1, p2) -> Float.compare(p1.distance, p2.distance)).orElse(null)) != null && closest.distance <= (float)this.runDistance && (result = this.startRun(mob, closest.runningFrom.getTileX(), closest.runningFrom.getTileY(), closest.runningFrom)) != null) {
            return this.alwaysReturnSuccess() ? AINodeResult.SUCCESS : result;
        }
        return this.alwaysReturnSuccess() ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
    }

    public AINodeResult startRun(T mob, int startTileX, int startTileY, float dx, float dy, ZoneTester zoneTester) {
        int runDistanceTile = (int)Math.ceil((float)this.runDistance / 32.0f);
        Point2D.Float dir = GameMath.normalize((float)dx, (float)dy);
        float dirMod = Math.abs(dir.x) > Math.abs(dir.y) ? 1.0f / Math.abs(dir.x) : 1.0f / Math.abs(dir.y);
        dir.x *= dirMod;
        dir.y *= dirMod;
        int radius = 8 + runDistanceTile;
        Point runPoint = WandererAINode.findWanderingPointAround(mob, (int)(startTileX + (int)(dir.x * (float)radius)), (int)(startTileY + (int)(dir.y * (float)radius)), (int)radius, (ZoneTester)zoneTester, (int)20, (int)5);
        this.nextRunCooldown = mob.getWorldEntity().getTime() + 2000L;
        if (runPoint != null) {
            return this.moveToTileTask(runPoint.x, runPoint.y, null, path -> {
                this.setRunning((Mob)mob, path.moveIfWithin(-1, -1, null));
                return this.alwaysReturnSuccess() ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
            });
        }
        runPoint = WandererAINode.findWanderingPointAround(mob, (int)startTileX, (int)startTileY, (int)(radius * 2), (ZoneTester)zoneTester, (int)20, (int)5);
        return runPoint != null ? this.moveToTileTask(runPoint.x, runPoint.y, null, path -> {
            this.setRunning((Mob)mob, path.moveIfWithin(-1, -1, null));
            return this.alwaysReturnSuccess() ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
        }) : null;
    }

    public AINodeResult startRun(T mob, int startTileX, int startTileY, float dx, float dy, Mob threat) {
        int runDistanceTile = (int)Math.ceil((float)this.runDistance / 32.0f);
        ZoneTester zoneTester = (tileX, tileY) -> {
            double distance = GameMath.diagonalMoveDistance((int)tileX, (int)tileY, (int)threat.getTileX(), (int)threat.getTileY());
            return distance >= (double)(runDistanceTile + 2);
        };
        return this.startRun(mob, startTileX, startTileY, dx, dy, zoneTester);
    }

    public AINodeResult startRun(T mob, int startTileX, int startTileY, Mob threat) {
        return this.startRun(mob, startTileX, startTileY, ((Mob)mob).x - threat.x, ((Mob)mob).y - threat.y, threat);
    }

    public void setRunning(Mob mob, boolean running) {
        if (running != this.isRunning) {
            this.isRunning = running;
            mob.buffManager.updateBuffs();
        }
    }

    public boolean alwaysReturnSuccess() {
        return false;
    }

    protected static class TempDistance {
        public final Mob runningFrom;
        public final float distance;

        public TempDistance(Mob runningFrom, Mob mob) {
            this.runningFrom = runningFrom;
            this.distance = runningFrom.getDistance(mob);
        }
    }
}

