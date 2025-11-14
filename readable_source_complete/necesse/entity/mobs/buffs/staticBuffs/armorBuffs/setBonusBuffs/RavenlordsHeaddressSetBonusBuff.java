/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.RavenlordsSetBonusBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RavenLordFeatherFollowingMob;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class RavenlordsHeaddressSetBonusBuff
extends RavenlordsSetBonusBuff {
    public FloatUpgradeValue featherDamage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(50.0f).setUpgradedValue(1.0f, 50.0f);
    public static int FEATHER_SPAWN_RUN_DISTANCE = 480;

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        buff.getGndData().setDouble("distanceRan", buff.owner.getDistanceRan());
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        double distanceRanSinceLastFeatherSpawn;
        double distanceRan;
        super.serverTick(buff);
        Mob owner = buff.owner;
        if (owner.isItemAttacker && (distanceRan = owner.getDistanceRan()) - (distanceRanSinceLastFeatherSpawn = buff.getGndData().getDouble("distanceRan")) > (double)FEATHER_SPAWN_RUN_DISTANCE) {
            this.summonFeather(buff, (ItemAttackerMob)owner);
            buff.getGndData().setDouble("distanceRan", distanceRan);
        }
    }

    private void summonFeather(ActiveBuff buff, ItemAttackerMob itemAttacker) {
        if (itemAttacker.isServer() && itemAttacker.serverFollowersManager.getFollowerCount("ravenlordfeather") < 6.0f) {
            RavenLordFeatherFollowingMob mob = new RavenLordFeatherFollowingMob();
            itemAttacker.serverFollowersManager.addFollower("ravenlordfeather", (Mob)mob, FollowPosition.FLYING_CIRCLE, "summonedmob", 1.0f, 6, null, false);
            mob.baseDamage = this.featherDamage.getValue(buff.getUpgradeTier()).floatValue();
            itemAttacker.getLevel().entityManager.addMob(mob, itemAttacker.x, itemAttacker.y);
        }
    }

    public static float getFinalDamage(Mob mob, float baseDamage) {
        return (mob.buffManager.getModifier(BuffModifiers.SPEED).floatValue() - 1.0f) * baseDamage;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
        float damage = this.featherDamage.getValue(currentValues.getUpgradeTier()).floatValue();
        if (currentValues.owner != null) {
            damage = RavenlordsHeaddressSetBonusBuff.getFinalDamage(currentValues.owner, damage) * GameDamage.getDamageModifier(currentValues.owner, DamageTypeRegistry.SUMMON);
        }
        DoubleItemStatTip ravenDamageTip = new DoubleItemStatTip(damage, 0){

            @Override
            public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                return new GameMessageBuilder().append(new LocalMessage("itemtooltip", "ravenlordset")).append("\n").append(new LocalMessage("itemtooltip", "ravenlordsetdamage", "damage", this.getReplaceValue(betterColor, worseColor, showDifference)));
            }
        };
        if (lastValues != null) {
            float compareDamage = this.featherDamage.getValue(lastValues.getUpgradeTier()).floatValue();
            if (lastValues.owner != null) {
                compareDamage = RavenlordsHeaddressSetBonusBuff.getFinalDamage(lastValues.owner, compareDamage) * GameDamage.getDamageModifier(currentValues.owner, DamageTypeRegistry.SUMMON);
            }
            ravenDamageTip.setCompareValue(compareDamage);
        }
        list.add(ravenDamageTip);
    }
}

