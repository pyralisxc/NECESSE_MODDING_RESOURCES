/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;

public class MobBeforeHitCalculatedEvent {
    public final Mob target;
    public final Attacker attacker;
    public final DamageType damageType;
    public int damage;
    public float knockbackX;
    public float knockbackY;
    public float knockbackAmount;
    public final boolean isCrit;
    public final GNDItemMap gndData;
    public boolean showDamageTip;
    public boolean playHitSound;
    private boolean prevented;

    public MobBeforeHitCalculatedEvent(MobBeforeHitEvent beforeHitEvent) {
        this.target = beforeHitEvent.target;
        this.attacker = beforeHitEvent.attacker;
        Attacker proxyAttacker = this.attacker == null ? null : this.attacker.getAttackerDamageProxy();
        GameDamage damage = beforeHitEvent.damage;
        boolean isCrit = GameRandom.globalRandom.getChance(damage == null ? 0.0f : damage.getBuffedCritChance(proxyAttacker));
        float critMod = 1.0f;
        if (isCrit) {
            Mob attackOwner = proxyAttacker == null ? null : proxyAttacker.getAttackOwner();
            critMod = attackOwner != null ? attackOwner.getCritDamageModifier() : ((Float)BuffModifiers.CRIT_DAMAGE.defaultBuffManagerValue).floatValue();
            critMod += damage == null ? 0.0f : damage.type.getTypeCritDamageModifier(proxyAttacker);
        }
        this.damageType = beforeHitEvent.damage.type;
        this.damage = damage == null ? 0 : damage.getTotalDamage(this.target, proxyAttacker, critMod);
        this.knockbackX = beforeHitEvent.knockbackX;
        this.knockbackY = beforeHitEvent.knockbackY;
        this.knockbackAmount = beforeHitEvent.knockbackAmount;
        this.isCrit = isCrit;
        this.showDamageTip = beforeHitEvent.showDamageTip;
        this.playHitSound = beforeHitEvent.playHitSound;
        this.prevented = beforeHitEvent.isPrevented();
        this.gndData = beforeHitEvent.gndData;
    }

    public void prevent() {
        this.prevented = true;
    }

    public boolean isPrevented() {
        return this.prevented;
    }

    public int getExpectedHealth() {
        if (this.prevented) {
            return this.target.getHealth();
        }
        return Math.max(this.target.getHealth() - this.damage, 0);
    }
}

