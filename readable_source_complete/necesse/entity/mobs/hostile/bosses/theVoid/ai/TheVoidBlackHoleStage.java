/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.TheVoidBlackHoleGroundEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;

public class TheVoidBlackHoleStage<T extends TheVoidMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public int maxRelativeOffset;

    public TheVoidBlackHoleStage(int maxRelativeOffset) {
        this.maxRelativeOffset = maxRelativeOffset;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        return AINodeResult.SUCCESS;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, "currentTarget");
        if (target == null) {
            return;
        }
        int beforeActiveTime = GameMath.lerp(((Mob)mob).getHealthPercent(), 800, 1200);
        int targetX = target.getX() + GameRandom.globalRandom.getIntBetween(-this.maxRelativeOffset, this.maxRelativeOffset);
        int targetY = target.getY() + GameRandom.globalRandom.getIntBetween(-this.maxRelativeOffset, this.maxRelativeOffset);
        TheVoidBlackHoleGroundEvent e = new TheVoidBlackHoleGroundEvent((Mob)mob, targetX, targetY, TheVoidMob.blackHoleDamage, beforeActiveTime, 10000 + beforeActiveTime, GameRandom.globalRandom);
        ((Entity)mob).getLevel().entityManager.events.add(e);
        ((TheVoidMob)mob).spawnedEvents.add(e);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

