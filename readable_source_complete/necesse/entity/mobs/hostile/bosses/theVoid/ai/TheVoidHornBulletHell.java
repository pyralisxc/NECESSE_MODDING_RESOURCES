/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid.ai;

import java.awt.Point;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidHornMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;
import necesse.entity.mobs.hostile.bosses.theVoid.ai.TheVoidKeepMovingAroundTargetStage;
import necesse.entity.projectile.AscendedBoltProjectile;

public class TheVoidHornBulletHell<T extends TheVoidMob>
extends TheVoidKeepMovingAroundTargetStage<T>
implements AttackStageInterface<T> {
    private int bulletsPerHorn;
    private int startupDelay;

    public TheVoidHornBulletHell(float keepRunningAndFindNewPositionsAtDistance) {
        super(keepRunningAndFindNewPositionsAtDistance);
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        this.tickFindNextPosition(mob, blackboard);
        if (this.startupDelay > 0) {
            this.startupDelay -= 50;
            return AINodeResult.RUNNING;
        }
        for (LevelMob<TheVoidHornMob> lm : ((TheVoidMob)mob).spawnedHorns) {
            TheVoidHornMob horn = lm.get(((Entity)mob).getLevel());
            if (horn == null || horn.isBroken) continue;
            this.fireProjectile(mob, horn.isLeftHorn);
        }
        --this.bulletsPerHorn;
        if (this.bulletsPerHorn <= 0) {
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.RUNNING;
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.bulletsPerHorn = GameMath.lerp(((Mob)mob).getHealthPercent(), 120, 60);
        this.startupDelay = GameMath.lerp(((Mob)mob).getHealthPercent(), 0, 1000);
        ((TheVoidMob)mob).showHornChargeUpAbility.runAndSend(this.startupDelay);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }

    private void fireProjectile(T mob, boolean isLeftHorn) {
        Point offset = ((TheVoidMob)mob).getHornOffset(isLeftHorn);
        AscendedBoltProjectile projectile = new AscendedBoltProjectile(((Entity)mob).getLevel(), ((TheVoidMob)mob).x + (float)offset.x, ((TheVoidMob)mob).y + (float)offset.y, GameRandom.globalRandom.getFloatBetween(0.0f, 360.0f), 200.0f, 2000, TheVoidMob.boltDamage, (Mob)mob);
        projectile.getUniqueID(GameRandom.globalRandom);
        ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
        ((TheVoidMob)mob).spawnedProjectiles.add(projectile);
        ((TheVoidMob)mob).magicBoltSoundAbility.runAndSend(((Entity)mob).getX() + offset.x, ((Entity)mob).getY() + offset.y);
    }
}

