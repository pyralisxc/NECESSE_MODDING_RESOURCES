/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.util.Comparator;
import java.util.stream.Stream;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ShieldTrinketBuff;
import necesse.entity.mobs.friendly.human.HumanMob;

public class VoodooShieldTrinketBuff
extends ShieldTrinketBuff {
    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        if (!buff.owner.isServer() || event.isPrevented() || !buff.owner.buffManager.hasBuff(BuffRegistry.SHIELD_ACTIVE)) {
            return;
        }
        int checkForMobsRange = 384;
        Mob mob = buff.owner;
        Stream<Mob> mobStream = mob.getLevel().entityManager.mobs.streamInRegionsInRange(mob.x, mob.y, checkForMobsRange);
        Stream playerStream = mob.getLevel().entityManager.players.streamInRegionsInRange(mob.x, mob.y, checkForMobsRange);
        Mob targetChosen = Stream.concat(mobStream, playerStream).filter(m -> {
            if (m.isHuman) {
                HumanMob hm = (HumanMob)m;
                return hm.isFriendlyHuman(hm);
            }
            if (m.isPlayer) {
                return m.isSameTeam(mob);
            }
            return false;
        }).filter(m -> m.getDistance(mob) <= (float)checkForMobsRange).max(Comparator.comparingInt(Mob::getHealth)).orElse(null);
        int finalDamage = event.damage / 2;
        if (targetChosen != null) {
            targetChosen.isServerHit(new GameDamage(event.damageType, (float)finalDamage), targetChosen.x, targetChosen.y, 0.0f, null);
            event.damage = 0;
            event.showDamageTip = false;
            event.playHitSound = false;
            event.gndData.setInt("redirectUniqueID", targetChosen.getUniqueID());
        } else {
            event.damage = finalDamage;
        }
        event.gndData.setItem("shieldItem", buff.getGndData().getItem("trinketItem"));
    }

    @Override
    public void onBeforeHit(ActiveBuff buff, MobBeforeHitEvent event) {
    }
}

