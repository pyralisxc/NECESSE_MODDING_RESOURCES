/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.modifiers.Modifier
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.gameDamageType.DamageType
 *  necesse.inventory.item.DoubleItemStatTip
 *  necesse.inventory.item.LocalMessageDoubleItemStatTip
 */
package aphorea.registry;

import aphorea.registry.AphModifiers;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class AphDamageType {
    public static DamageType INSPIRATION;

    public static void registerCore() {
        INSPIRATION = new InspirationDamageType();
        DamageTypeRegistry.registerDamageType((String)"inspiration", (DamageType)INSPIRATION);
    }

    public static class InspirationDamageType
    extends DamageType {
        public Modifier<Float> getBuffDamageModifier() {
            return AphModifiers.INSPIRATION_DAMAGE;
        }

        public Modifier<Float> getBuffAttackSpeedModifier(Attacker attacker) {
            return null;
        }

        public Modifier<Float> getBuffCritChanceModifier() {
            return AphModifiers.INSPIRATION_CRIT_CHANCE;
        }

        public Modifier<Float> getBuffCritDamageModifier() {
            return AphModifiers.INSPIRATION_CRIT_DAMAGE;
        }

        public GameMessage getStatsText() {
            return new LocalMessage("stats", "inspiration_damage");
        }

        public DoubleItemStatTip getDamageTip(int damage) {
            return new LocalMessageDoubleItemStatTip("itemtooltip", "inspirationdamagetip", "value", (double)damage, 0);
        }

        public String getSteamStatKey() {
            return "inspiration_damage_dealt";
        }
    }
}

