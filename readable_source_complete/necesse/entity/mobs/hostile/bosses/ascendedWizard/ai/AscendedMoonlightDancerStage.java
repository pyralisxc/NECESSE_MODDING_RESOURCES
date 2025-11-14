/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.ai;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.pickup.AscendedStarPickupEntity;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class AscendedMoonlightDancerStage<T extends AscendedWizardMob>
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
        ((AscendedWizardMob)mob).setShieldedAbility.runAndSend(mob.getTime() + 10000L);
        GameUtils.streamServerClients(((Entity)mob).getServer(), ((Entity)mob).getLevel()).forEach(c -> {
            PlayerMob player = c.playerMob;
            int duration = GameMath.lerp(mob.getHealthPercent(), 8000, 12000);
            ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.ASCENDED_DARKNESS, (Mob)player, duration, (Attacker)mob);
            int stacks = 4;
            ab.setStacks(stacks, duration, (Attacker)mob);
            ab.getGndData().setInt("uniqueID", mob.getUniqueID());
            player.buffManager.addBuff(ab, true);
            float anglePerPickup = 360.0f / (float)stacks;
            for (int i = 0; i < stacks; ++i) {
                float range = GameRandom.globalRandom.getFloatBetween(7.0f, 12.0f) * 32.0f;
                float currentAngle = anglePerPickup * (float)i + GameRandom.globalRandom.nextFloat() * anglePerPickup;
                Point2D.Float dir = GameMath.getAngleDir(currentAngle);
                Ray<LevelObjectHit> firstHit = GameUtils.castRayFirstHit(mob.getLevel(), (double)player.x, (double)player.y, (double)dir.x, (double)dir.y, (double)range, new CollisionFilter().mobCollision());
                Point spawnPoint = firstHit == null ? new Point((int)(player.x + dir.x * range), (int)(player.y + dir.y * range)) : new Point((int)firstHit.x2 - (int)(dir.x * 32.0f), (int)firstHit.y2 - (int)(dir.y * 32.0f));
                AscendedStarPickupEntity pickup = new AscendedStarPickupEntity(mob.getLevel(), spawnPoint.x, spawnPoint.y, 0.0f, 0.0f);
                mob.getLevel().entityManager.pickups.add(pickup);
            }
        });
        ((AscendedWizardMob)mob).playBossSoundAbility.runAndSend(AscendedWizardMob.BossSound.MOONLIGHT_DANCER);
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }
}

