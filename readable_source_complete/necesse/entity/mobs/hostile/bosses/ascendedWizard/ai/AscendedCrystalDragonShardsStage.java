/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import java.awt.geom.Point2D;
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
import necesse.entity.projectile.AscendedShardBombProjectile;

public class AscendedCrystalDragonShardsStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
    public float minBombsPerSec = 4.0f;
    public float maxBombsPerSec = 6.0f;
    public int minBombs = 20;
    public int maxBombs = 30;
    public float nextFireBuffer;
    public int totalFired;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        float totalProjectiles = GameMath.lerp(((Mob)mob).getHealthPercent(), this.maxBombs, this.minBombs);
        if ((float)this.totalFired >= totalProjectiles) {
            return AINodeResult.SUCCESS;
        }
        GameRandom random = GameRandom.globalRandom;
        float projectilesPerSec = GameMath.lerp(((Mob)mob).getHealthPercent(), this.maxBombsPerSec, this.minBombsPerSec);
        float projectilesPerTick = projectilesPerSec * 50.0f / 1000.0f;
        this.nextFireBuffer += projectilesPerTick;
        while (this.nextFireBuffer >= 1.0f) {
            this.nextFireBuffer -= 1.0f;
            ++this.totalFired;
            float randomAngle = random.getFloatBetween(0.0f, 360.0f);
            Point2D.Float dir = GameMath.getAngleDir(randomAngle);
            float randomDistance = random.getFloatBetween(200.0f, 600.0f);
            int targetX = (int)((float)((Entity)mob).getX() + dir.x * randomDistance);
            int targetY = (int)((float)((Entity)mob).getY() + dir.y * randomDistance);
            AscendedShardBombProjectile projectile = new AscendedShardBombProjectile(((AscendedWizardMob)mob).x, ((AscendedWizardMob)mob).y, targetX, targetY, 100, (int)randomDistance, AscendedWizardMob.shardBombDamage, (Mob)mob);
            ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
            ((AscendedWizardMob)mob).spawnedProjectiles.add(projectile);
            ((AscendedWizardMob)mob).playLightningBoltSoundAbility.runAndSend();
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.totalFired = 0;
        Point baseTile = ((AscendedWizardMob)mob).getBaseTile(blackboard);
        int centerX = baseTile.x * 32 + 16;
        int centerY = baseTile.y * 32 + 16;
        int radius = 384;
        float speed = MobMovementCircleLevelPos.convertToRotSpeed(radius, ((Mob)mob).getSpeed());
        blackboard.mover.setCustomMovement(this, new MobMovementCircleLevelPos((Mob)mob, centerX, centerY, radius, speed, GameRandom.globalRandom.nextBoolean()));
        ((AscendedWizardMob)mob).playBossSoundAbility.runAndSend(AscendedWizardMob.BossSound.CRYSTAL_DRAGON);
        ((AscendedWizardMob)mob).startArmsUpAnimation.runAndSend();
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            blackboard.mover.stopMoving((Mob)mob);
        }
        ((AscendedWizardMob)mob).startArmsDownAnimation.runAndSend();
    }
}

