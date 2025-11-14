/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

public class IvyCircletSetBonusBuff
extends SetBonusBuff {
    public float secondsPerSlime = 1.5f;
    public FloatUpgradeValue slimeDamage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(20.0f).setUpgradedValue(1.0f, 45.0f);
    public IntUpgradeValue maxSummons = new IntUpgradeValue().setBaseValue(1).setUpgradedValue(1.0f, 2);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_SUMMONS, this.maxSummons.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        float chance;
        super.serverTick(buff);
        if (buff.owner.isItemAttacker && buff.owner.isInCombat() && GameRandom.globalRandom.getChance(chance = 1.0f / this.secondsPerSlime / 20.0f)) {
            ItemAttackerMob attackerMob = (ItemAttackerMob)buff.owner;
            Level level = buff.owner.getLevel();
            AttackingFollowingMob mob = (AttackingFollowingMob)MobRegistry.getMob("playerpoisonslime", level);
            attackerMob.serverFollowersManager.addFollower("playerivypoisonslime", (Mob)mob, FollowPosition.WALK_CLOSE, "summonedmob", 1.0f, p -> 100, null, false);
            Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(mob, level, attackerMob.x, attackerMob.y);
            mob.updateDamage(new GameDamage(DamageTypeRegistry.SUMMON, this.slimeDamage.getValue(buff.getUpgradeTier()).floatValue()));
            mob.setRemoveWhenNotInInventory(ItemRegistry.getItem("ivycirclet"), CheckSlotType.HELMET);
            mob.getLevel().entityManager.addMob(mob, spawnPoint.x, spawnPoint.y);
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ivycircletset"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

