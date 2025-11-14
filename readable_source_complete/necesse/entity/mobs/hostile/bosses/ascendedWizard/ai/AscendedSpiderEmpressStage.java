/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.projectile.AscendedSlashProjectile;
import necesse.entity.projectile.EmpressSlashWarningProjectile;

public class AscendedSpiderEmpressStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public long warningTime;
    private int slashesRemaining;
    private final ArrayList<Float> attackAngles = new ArrayList();
    private final ArrayList<Point> attackPos = new ArrayList();
    private float warningBuffer;
    private float slashBuffer;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        float angle;
        Mob target = blackboard.getObject(Mob.class, "currentTarget");
        if (target == null) {
            return AINodeResult.RUNNING;
        }
        if (mob.getTime() >= this.warningTime) {
            if (this.attackAngles.isEmpty()) {
                return AINodeResult.SUCCESS;
            }
            this.slashBuffer += 50.0f;
            if (this.slashBuffer > 100.0f) {
                this.slashBuffer -= 100.0f;
                angle = this.attackAngles.get(0).floatValue();
                int attackX = this.attackPos.get((int)0).x;
                int attackY = this.attackPos.get((int)0).y;
                AscendedSlashProjectile slashProjectile = new AscendedSlashProjectile(attackX, attackY, angle, AscendedWizardMob.spiderSlashDamage, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(slashProjectile);
                ((AscendedWizardMob)mob).spawnedProjectiles.add(slashProjectile);
                AscendedSlashProjectile slashProjectileReverse = new AscendedSlashProjectile(attackX, attackY, angle - 180.0f, AscendedWizardMob.spiderSlashDamage, (Mob)mob);
                ((Entity)mob).getLevel().entityManager.projectiles.add(slashProjectileReverse);
                ((AscendedWizardMob)mob).spawnedProjectiles.add(slashProjectileReverse);
                ((AscendedWizardMob)mob).slashSoundAbility.runAndSend((int)((float)attackX + slashProjectile.dx * (float)slashProjectile.distance / 2.0f), (int)((float)attackY + slashProjectile.dy * (float)slashProjectile.distance / 2.0f));
                this.attackAngles.remove(0);
                this.attackPos.remove(0);
            }
            if (this.slashesRemaining < 1) {
                return AINodeResult.RUNNING;
            }
        }
        GameRandom random = GameRandom.globalRandom;
        this.warningBuffer += 50.0f;
        while (this.slashesRemaining > 0 && this.warningBuffer > 100.0f) {
            int randomX = random.getIntBetween(-200, 200);
            int randomY = random.getIntBetween(-200, 200);
            angle = random.getIntBetween(-180, 180);
            EmpressSlashWarningProjectile slashProjectile = new EmpressSlashWarningProjectile((int)(target.x + (float)randomX), (int)(target.y + (float)randomY), angle, AscendedWizardMob.spiderSlashDamage, (Mob)mob);
            ((Entity)mob).getLevel().entityManager.projectiles.add(slashProjectile);
            ((AscendedWizardMob)mob).spawnedProjectiles.add(slashProjectile);
            EmpressSlashWarningProjectile slashProjectileReverse = new EmpressSlashWarningProjectile((int)(target.x + (float)randomX), (int)(target.y + (float)randomY), angle - 180.0f, AscendedWizardMob.spiderSlashDamage, (Mob)mob);
            ((Entity)mob).getLevel().entityManager.projectiles.add(slashProjectileReverse);
            ((AscendedWizardMob)mob).spawnedProjectiles.add(slashProjectileReverse);
            this.attackAngles.add(Float.valueOf(angle));
            this.attackPos.add(new Point((int)(target.x + (float)randomX), (int)(target.y + (float)randomY)));
            this.warningBuffer -= 100.0f;
            --this.slashesRemaining;
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.slashesRemaining = ((AscendedWizardMob)mob).isTransformed() ? 40 : 20;
        this.warningTime = mob.getTime() + 750L;
        ((AscendedWizardMob)mob).playBossSoundAbility.runAndSend(AscendedWizardMob.BossSound.SPIDER_EMPRESS);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

