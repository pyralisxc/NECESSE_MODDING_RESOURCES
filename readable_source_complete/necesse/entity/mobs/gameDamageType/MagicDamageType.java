/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.gameDamageType;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class MagicDamageType
extends DamageType {
    @Override
    public Modifier<Float> getBuffDamageModifier() {
        return BuffModifiers.MAGIC_DAMAGE;
    }

    @Override
    public Modifier<Float> getBuffAttackSpeedModifier(Attacker attacker) {
        return BuffModifiers.MAGIC_ATTACK_SPEED;
    }

    @Override
    public float getTypeFinalAttackSpeedModifier(Attacker attacker) {
        Mob attackOwner;
        Mob mob = attackOwner = attacker != null ? attacker.getAttackOwner() : null;
        if (attackOwner != null && attackOwner.buffManager.getModifier(BuffModifiers.MANA_EXHAUSTED).booleanValue()) {
            return 0.5f;
        }
        return super.getTypeFinalAttackSpeedModifier(attacker);
    }

    @Override
    public Modifier<Float> getBuffCritChanceModifier() {
        return BuffModifiers.MAGIC_CRIT_CHANCE;
    }

    @Override
    public Modifier<Float> getBuffCritDamageModifier() {
        return BuffModifiers.MAGIC_CRIT_DAMAGE;
    }

    @Override
    public GameMessage getStatsText() {
        return new LocalMessage("stats", "magic_damage");
    }

    @Override
    public DoubleItemStatTip getDamageTip(int damage) {
        return new LocalMessageDoubleItemStatTip("itemtooltip", "magicdamagetip", "value", damage, 0);
    }

    @Override
    public String getSteamStatKey() {
        return "magic_damage_dealt";
    }
}

