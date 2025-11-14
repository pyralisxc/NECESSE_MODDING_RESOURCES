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

public class SummonDamageType
extends DamageType {
    @Override
    public Modifier<Float> getBuffDamageModifier() {
        return BuffModifiers.SUMMON_DAMAGE;
    }

    @Override
    public Modifier<Float> getBuffAttackSpeedModifier(Attacker attacker) {
        return BuffModifiers.SUMMON_ATTACK_SPEED;
    }

    @Override
    public Modifier<Float> getBuffCritChanceModifier() {
        return BuffModifiers.SUMMON_CRIT_CHANCE;
    }

    @Override
    public Modifier<Float> getBuffCritDamageModifier() {
        return BuffModifiers.SUMMON_CRIT_DAMAGE;
    }

    @Override
    public GameMessage getStatsText() {
        return new LocalMessage("stats", "summon_damage");
    }

    @Override
    public DoubleItemStatTip getDamageTip(int damage) {
        return new LocalMessageDoubleItemStatTip("itemtooltip", "summondamagetip", "value", damage, 0);
    }

    @Override
    public String getSteamStatKey() {
        return "summon_damage_dealt";
    }
}

