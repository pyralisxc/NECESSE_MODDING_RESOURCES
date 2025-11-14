/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class AscendedFlyToCenterStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public boolean waitForArrive;
    private boolean hasStartedMoving;

    public AscendedFlyToCenterStage(boolean waitForArrive) {
        this.waitForArrive = waitForArrive;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Point tile = ((Mob)mob).getWanderBaseTile();
        if (!this.hasStartedMoving && tile != null) {
            this.hasStartedMoving = true;
            Point pos = ((AscendedWizardMob)mob).findNewTile(blackboard);
            if (pos != null) {
                blackboard.mover.directMoveTo(this, tile.x * 32 + 16, tile.y * 32 + 16);
                return this.waitForArrive ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
            }
        }
        if (this.waitForArrive && blackboard.mover.isCurrentlyMovingFor(this) && blackboard.mover.isMoving()) {
            return AINodeResult.RUNNING;
        }
        return AINodeResult.SUCCESS;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.hasStartedMoving = false;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

