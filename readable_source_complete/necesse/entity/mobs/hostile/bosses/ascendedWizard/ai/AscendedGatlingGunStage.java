/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.projectile.AscendedBoltProjectile;

public class AscendedGatlingGunStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    private int projectileCount;
    private int projectilesShot;
    private int buffer;
    private int fireRate;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.fireRate = GameMath.lerp(((Mob)mob).getHealthPercent(), 100, 50);
        this.projectileCount = ((AscendedWizardMob)mob).isTransformed() ? 50 : 30;
        this.projectilesShot = 0;
        ((AscendedWizardMob)mob).startArmsUpAnimation.runAndSend();
        if (((AscendedWizardMob)mob).isTransformed()) {
            Point baseTile = ((AscendedWizardMob)mob).getBaseTile(blackboard);
            int centerX = baseTile.x * 32 + 16;
            int centerY = baseTile.y * 32 + 16;
            int radius = 384;
            float speed = MobMovementCircleLevelPos.convertToRotSpeed(radius, ((Mob)mob).getSpeed());
            blackboard.mover.setCustomMovement(this, new MobMovementCircleLevelPos((Mob)mob, centerX, centerY, radius, speed, GameRandom.globalRandom.nextBoolean()));
        }
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        if (this.projectileCount > this.projectilesShot) {
            this.buffer += 50;
            if (this.buffer > this.fireRate) {
                Mob target = blackboard.getObject(Mob.class, "currentTarget");
                GameRandom random = GameRandom.globalRandom;
                AscendedBoltProjectile projectile = new AscendedBoltProjectile(((Entity)mob).getLevel(), ((AscendedWizardMob)mob).x, ((AscendedWizardMob)mob).y, target.x + (float)random.getIntBetween(-50, 50), target.y + (float)random.getIntBetween(-50, 50), 200.0f, 1000, AscendedWizardMob.boltDamage, (Mob)mob);
                projectile.getUniqueID(random);
                ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                ((AscendedWizardMob)mob).spawnedProjectiles.add(projectile);
                ((AscendedWizardMob)mob).laserBoltSoundAbility.runAndSend(projectile.getX(), projectile.getY());
                ++this.projectilesShot;
                this.buffer = 0;
            }
            return AINodeResult.RUNNING;
        }
        return AINodeResult.SUCCESS;
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            blackboard.mover.stopMoving((Mob)mob);
        }
        ((AscendedWizardMob)mob).startArmsDownAnimation.runAndSend();
    }
}

