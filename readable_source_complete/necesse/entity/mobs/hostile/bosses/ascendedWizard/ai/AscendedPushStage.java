/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.levelEvent.explosionEvent.AscendedPushExplosionEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class AscendedPushStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        int range;
        int knockback = (int)((float)(((AscendedWizardMob)mob).isTransformed() ? 1500 : 750) * (1.0f + ((Mob)mob).getHealthPercent()));
        int n = range = ((AscendedWizardMob)mob).isTransformed() ? 300 : 200;
        if (GameUtils.streamServerClients(((Entity)mob).getServer(), ((Entity)mob).getLevel()).anyMatch(c -> GameMath.diamondDistance(c.playerMob.x, c.playerMob.y, mob.x, mob.y) < (float)range)) {
            ((Entity)mob).getLevel().entityManager.events.add(new AscendedPushExplosionEvent(((AscendedWizardMob)mob).x, ((AscendedWizardMob)mob).y, range, knockback, (Mob)mob));
        }
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        return AINodeResult.SUCCESS;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

