/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.gameDamageType;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class TrueDamageType
extends DamageType {
    @Override
    public Modifier<Float> getBuffDamageModifier() {
        return null;
    }

    @Override
    public Modifier<Float> getBuffAttackSpeedModifier(Attacker attacker) {
        return null;
    }

    @Override
    public Modifier<Float> getBuffCritChanceModifier() {
        return null;
    }

    @Override
    public Modifier<Float> getBuffCritDamageModifier() {
        return null;
    }

    @Override
    public GameMessage getStatsText() {
        return new LocalMessage("stats", "true_damage");
    }

    @Override
    public DoubleItemStatTip getDamageTip(int damage) {
        return new LocalMessageDoubleItemStatTip("itemtooltip", "truedamagetip", "value", damage, 0);
    }

    @Override
    public float getDamageReduction(Mob target, Attacker attacker, GameDamage damage) {
        return 0.0f;
    }

    @Override
    public String getSteamStatKey() {
        return "true_damage_dealt";
    }
}

