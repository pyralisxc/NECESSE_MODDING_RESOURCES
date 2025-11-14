/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.projectile.AscendedGolemSpawnProjectile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class AscendedCrystalDragonGolemsStage<T extends AscendedWizardMob>
extends AINode<T>
implements AttackStageInterface<T> {
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
        int spawns = ((AscendedWizardMob)mob).isTransformed() ? 8 : 4;
        float anglePerPickup = 360.0f / (float)spawns;
        for (int i = 0; i < spawns; ++i) {
            float range = GameRandom.globalRandom.getFloatBetween(7.0f, 12.0f) * 32.0f;
            float currentAngle = anglePerPickup * (float)i + GameRandom.globalRandom.nextFloat() * anglePerPickup;
            Point2D.Float dir = GameMath.getAngleDir(currentAngle);
            Ray<LevelObjectHit> firstHit = GameUtils.castRayFirstHit(((Entity)mob).getLevel(), (double)((AscendedWizardMob)mob).x, (double)((AscendedWizardMob)mob).y, (double)dir.x, (double)dir.y, (double)range, new CollisionFilter().mobCollision());
            Point spawnPoint = firstHit == null ? new Point((int)(((AscendedWizardMob)mob).x + dir.x * range), (int)(((AscendedWizardMob)mob).y + dir.y * range)) : new Point((int)firstHit.x2 - (int)(dir.x * 32.0f), (int)firstHit.y2 - (int)(dir.y * 32.0f));
            int distance = (int)((Mob)mob).getDistance(spawnPoint.x, spawnPoint.y);
            AscendedGolemSpawnProjectile projectile = new AscendedGolemSpawnProjectile(((Entity)mob).getLevel(), (Mob)mob, spawnPoint.x, spawnPoint.y, 30.0f, distance, new GameDamage(0.0f), 50);
            ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
            ((AscendedWizardMob)mob).spawnedProjectiles.add(projectile);
        }
        ((AscendedWizardMob)mob).playBossSoundAbility.runAndSend(AscendedWizardMob.BossSound.CRYSTAL_DRAGON);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

