/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.util.HashMap;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.BloodGrimoireParticleLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class ReturnLifeOnHitLevelEvent
extends MouseBeamLevelEvent {
    public int lifeSteal = 0;

    public ReturnLifeOnHitLevelEvent() {
    }

    public ReturnLifeOnHitLevelEvent(Mob owner, int startTargetX, int startTargetY, int seed, float speed, float distance, GameDamage damage, int knockback, HashMap<Integer, Long> mobHits, int hitCooldown, float appendAttackSpeedModifier, int bounces, float resilienceGain, Color color, int lifeSteal) {
        super(owner, startTargetX, startTargetY, seed, speed, distance, damage, knockback, mobHits, hitCooldown, appendAttackSpeedModifier, bounces, resilienceGain, color);
        this.lifeSteal = lifeSteal;
    }

    @Override
    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        this.mobHits.put(target.getHitCooldownUniqueID(), this.owner.getTime());
        target.isServerHit(this.damage, target.x - this.owner.x, target.y - this.owner.y, this.knockback, this.owner);
        if (target.canGiveResilience(this.owner) && this.resilienceGain != 0.0f && target.getTime() >= this.lastResilienceGainTime + (long)this.hitCooldown) {
            this.owner.addResilience(this.resilienceGain);
            this.lastResilienceGainTime = target.getTime();
        }
        if (target.canGiveLifeSteal(this.owner) && this.lifeSteal != 0) {
            int newHealth = this.owner.getHealth() + this.lifeSteal;
            this.owner.setHealth(newHealth, 0.0f, 0.0f, target);
        }
        if (target.buffManager.hasBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_MARKED_DEBUFF)) {
            int checkForMobsRange = 256;
            GameUtils.streamTargetsRange(this.owner, target.getX(), target.getY(), checkForMobsRange).filter(mob -> mob != target).filter(mob -> mob.buffManager.hasBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_MARKED_DEBUFF)).filter(mob -> !mob.isOnGenericCooldown("bloodgrimoiretick")).filter(mob -> mob.getDistance(target.x, target.y) <= (float)checkForMobsRange).forEach(mob -> {
                mob.isServerHit(this.damage.modFinalMultiplier(0.5f), mob.x - this.owner.x, mob.y - this.owner.y, this.knockback, this.owner);
                this.level.entityManager.events.add(new BloodGrimoireParticleLevelEvent((Mob)mob, target));
                mob.startGenericCooldown("bloodgrimoiretick", this.getHitCooldown());
            });
        }
    }
}

