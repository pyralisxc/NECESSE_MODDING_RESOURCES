/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.gameDamageType;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class MeleeDamageType
extends DamageType {
    @Override
    public Modifier<Float> getBuffDamageModifier() {
        return BuffModifiers.MELEE_DAMAGE;
    }

    @Override
    public Modifier<Float> getBuffAttackSpeedModifier(Attacker attacker) {
        return BuffModifiers.MELEE_ATTACK_SPEED;
    }

    @Override
    public Modifier<Float> getBuffCritChanceModifier() {
        return BuffModifiers.MELEE_CRIT_CHANCE;
    }

    @Override
    public Modifier<Float> getBuffCritDamageModifier() {
        return BuffModifiers.MELEE_CRIT_DAMAGE;
    }

    @Override
    public GameMessage getStatsText() {
        return new LocalMessage("stats", "melee_damage");
    }

    @Override
    public DoubleItemStatTip getDamageTip(int damage) {
        return new LocalMessageDoubleItemStatTip("itemtooltip", "meleedamagetip", "value", damage, 0);
    }

    @Override
    public String getSteamStatKey() {
        return "melee_damage_dealt";
    }
}

