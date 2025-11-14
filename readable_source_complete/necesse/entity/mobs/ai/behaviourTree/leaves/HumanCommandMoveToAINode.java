/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.Entity;
import necesse.entity.mobs.ai.behaviourTree.leaves.CommandMoveToAINode;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanCommandMoveToAINode<T extends HumanMob>
extends CommandMoveToAINode<T> {
    public boolean isBeforeChaser;

    public HumanCommandMoveToAINode(boolean isBeforeChaser) {
        this.isBeforeChaser = isBeforeChaser;
    }

    @Override
    public Point getLevelPosition(T mob) {
        if (((HumanMob)mob).isHiding && mob.isSettlerOnCurrentLevel()) {
            return null;
        }
        if (this.isBeforeChaser && !((HumanMob)mob).commandMoveToGuardPoint) {
            return null;
        }
        if (((HumanMob)mob).commandGuardPoint != null) {
            if (((HumanMob)mob).objectUser != null) {
                ((HumanMob)mob).objectUser.stopUsing();
            }
            return ((HumanMob)mob).commandGuardPoint;
        }
        return null;
    }

    @Override
    public void onArrived(T mob) {
        ((HumanMob)mob).commandMoveToGuardPoint = false;
    }

    @Override
    public void tickMoving(T mob) {
        ((HumanMob)mob).resetCommandsBuffer = Math.max(180000, ((HumanMob)mob).resetCommandsBuffer);
    }

    @Override
    public void tickStandingStill(T mob) {
    }

    @Override
    public void onCannotMoveTo(T mob, int targetX, int targetY) {
        Point tile = HumanCommandMoveToAINode.findClosestMoveToTile(mob, targetX, targetY);
        if (tile != null) {
            ((HumanMob)mob).commandGuardPoint = new Point(tile.x * 32 + 16, tile.y * 32 + 16);
            this.nextPathFindTime = 0L;
            this.nextCheckArrivedTime = 0L;
        } else {
            ((HumanMob)mob).commandGuardPoint = new Point(((Entity)mob).getX(), ((Entity)mob).getY());
        }
    }

    @Override
    public void updateActivityDescription(T mob, GameMessage description) {
        if (description == null) {
            ((HumanMob)mob).clearActivity("command");
        } else {
            ((HumanMob)mob).setActivity("command", 15000, description);
        }
    }
}

