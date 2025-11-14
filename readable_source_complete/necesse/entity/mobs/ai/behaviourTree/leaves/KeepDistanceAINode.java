/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.MovedRectangle;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class KeepDistanceAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public String targetKey;
    public boolean moveAwayFromHits = true;
    public int maxTargetDistance;
    public String chaserTargetKey = "chaserTarget";
    protected boolean movingAway = false;

    public KeepDistanceAINode(String targetKey, int maxTargetDistance) {
        this.targetKey = targetKey;
        this.maxTargetDistance = maxTargetDistance;
    }

    public KeepDistanceAINode(int maxTargetDistance) {
        this("currentTarget", maxTargetDistance);
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onEvent("resetTarget", e -> this.getBlackboard().put(this.chaserTargetKey, null));
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        blackboard.put(this.chaserTargetKey, null);
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, this.targetKey);
        boolean forceMoveAway = false;
        if (this.moveAwayFromHits) {
            for (AIWasHitEvent e : blackboard.getLastHits()) {
                Mob attacker = e.event.attacker != null ? e.event.attacker.getAttackOwner() : null;
                if (attacker == null) continue;
                target = attacker;
                forceMoveAway = true;
                break;
            }
        }
        if (target != null) {
            float distance = ((Mob)mob).getDistance(target);
            if (distance < (float)this.maxTargetDistance || forceMoveAway) {
                Point2D.Float targetDir = GameMath.normalize(((Mob)mob).x - target.x, ((Mob)mob).y - target.y);
                float angle = GameMath.getAngle(targetDir);
                int totalDirections = 8;
                int totalDirectionsAngle = 360 / totalDirections;
                angle = GameMath.fixAngle(angle + (float)totalDirectionsAngle / 2.0f);
                int dir = (int)(angle / (float)totalDirectionsAngle);
                Point2D.Float finalDir = new Point2D.Float();
                for (int i = 0; i < totalDirections / 2; ++i) {
                    int currentDir = dir - i;
                    finalDir = GameMath.getAngleDir(currentDir * totalDirectionsAngle);
                    int targetXPos = (int)(((Mob)mob).x + finalDir.x * (float)this.maxTargetDistance);
                    int targetYPos = (int)(((Mob)mob).y + finalDir.y * (float)this.maxTargetDistance);
                    if (!((Entity)mob).getLevel().collides((Shape)new MovedRectangle((Mob)mob, targetXPos, targetYPos), ((Mob)mob).getLevelCollisionFilter())) break;
                    if (i == 0) continue;
                    currentDir = dir + i;
                    finalDir = GameMath.getAngleDir(currentDir * totalDirectionsAngle);
                    targetXPos = (int)(((Mob)mob).x + finalDir.x * (float)this.maxTargetDistance);
                    targetYPos = (int)(((Mob)mob).y + finalDir.y * (float)this.maxTargetDistance);
                    if (!((Entity)mob).getLevel().collides((Shape)new MovedRectangle((Mob)mob, targetXPos, targetYPos), ((Mob)mob).getLevelCollisionFilter())) break;
                }
                float walkAwayDistance = (forceMoveAway ? distance : 0.0f) + (float)this.maxTargetDistance + (float)((Mob)mob).moveAccuracy;
                blackboard.mover.setCustomMovement(this, new MobMovementRelative(target, finalDir.x * walkAwayDistance, finalDir.y * walkAwayDistance));
                this.movingAway = true;
            }
        } else {
            this.movingAway = false;
        }
        if (this.movingAway) {
            this.getBlackboard().put(this.chaserTargetKey, target);
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.FAILURE;
    }
}

