/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.projectile.AscendedBeamProjectile;

public class AscendedWizardOrbitalBeamStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    private Mob target;
    private Point startPoint;
    private int currentChargeTime;
    private int chargeTime;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.target = blackboard.getObject(Mob.class, "currentTarget");
        this.chargeTime = ((AscendedWizardMob)mob).isTransformed() ? 500 : 1500;
        this.currentChargeTime = 0;
        this.startPoint = new Point(this.target.getX() + GameRandom.globalRandom.getIntBetween(-50, 50), this.target.getY() + GameRandom.globalRandom.getIntBetween(-50, 50));
        ((AscendedWizardMob)mob).startBeamSpawningAnimationAbility.runAndSend(this.startPoint.x, this.startPoint.y, this.chargeTime);
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.currentChargeTime > this.chargeTime) {
            GameRandom random = GameRandom.globalRandom;
            AscendedBeamProjectile projectile = new AscendedBeamProjectile(this.startPoint.x, this.startPoint.y, this.target, 50.0f, ((AscendedWizardMob)mob).isTransformed() ? 4000 : 2000, AscendedWizardMob.boltDamage, 100, ((AscendedWizardMob)mob).isTransformed(), (Mob)mob);
            projectile.getUniqueID(random);
            ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
            ((AscendedWizardMob)mob).spawnedProblematicProjectiles.add(projectile);
            return AINodeResult.SUCCESS;
        }
        this.currentChargeTime += 50;
        return AINodeResult.RUNNING;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

