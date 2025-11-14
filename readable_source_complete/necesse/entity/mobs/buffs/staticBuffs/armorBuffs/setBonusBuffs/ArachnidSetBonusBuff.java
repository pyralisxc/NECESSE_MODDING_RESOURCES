/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class ArachnidSetBonusBuff
extends SetBonusBuff {
    private final String summonType = "arachnidspiderling";
    public IntUpgradeValue maxSummons = new IntUpgradeValue().setBaseValue(1).setUpgradedValue(1.0f, 2);
    public FloatUpgradeValue damage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(28.0f).setUpgradedValue(1.0f, 50.0f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_SUMMONS, this.maxSummons.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.checkAndUpdateSpawnedSpiderlings(buff);
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "arachnidset"), 400);
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).excludeLimits(BuffModifiers.SLOW).buildToStatList(list);
    }

    @Override
    public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, GNDItemMap attackMap) {
        super.onItemAttacked(buff, targetX, targetY, attackerMob, attackHeight, item, slot, animAttack, attackMap);
        if (attackerMob.isClient()) {
            return;
        }
        if (!buff.owner.isItemAttacker) {
            return;
        }
        this.updateSpawnedSpiderlings(buff);
    }

    protected void checkAndUpdateSpawnedSpiderlings(ActiveBuff buff) {
        int currentActiveSummons = this.getCurrentActiveSummons((ItemAttackerMob)buff.owner);
        GNDItemMap gndData = buff.getGndData();
        int lastOccupiedSummons = gndData.getInt("lastOccupiedSummons");
        if (lastOccupiedSummons != currentActiveSummons) {
            this.updateSpawnedSpiderlings(buff);
            gndData.setInt("lastOccupiedSummons", currentActiveSummons);
        }
    }

    protected int getCurrentSpiderlings(ItemAttackerMob attackerMob) {
        return (int)attackerMob.serverFollowersManager.streamFollowers().filter(f -> f.summonType.equals("arachnidspiderling")).count();
    }

    protected int getCurrentActiveSummons(ItemAttackerMob attackerMob) {
        return (int)attackerMob.serverFollowersManager.getFollowerCount("summonedmob");
    }

    protected int getMaxSpiderlings(ItemAttackerMob attackerMob) {
        return this.getCurrentActiveSummons(attackerMob) / 3;
    }

    protected void updateSpawnedSpiderlings(ActiveBuff buff) {
        if (!buff.owner.isItemAttacker) {
            return;
        }
        ItemAttackerMob attackerMob = (ItemAttackerMob)buff.owner;
        int currentSpiderlings = this.getCurrentSpiderlings(attackerMob);
        int desiredSpiderlings = this.getMaxSpiderlings(attackerMob);
        for (int i = currentSpiderlings; i < desiredSpiderlings; ++i) {
            this.spawnSpiderling(buff.owner, buff);
        }
    }

    private void spawnSpiderling(Mob owner, ActiveBuff buff) {
        if (owner.isClient()) {
            return;
        }
        GameDamage spiderDamage = new GameDamage(this.damage.getValue(buff.getUpgradeTier()).floatValue());
        AttackingFollowingMob spiderling = (AttackingFollowingMob)MobRegistry.getMob("arachnidspider", owner.getLevel());
        ((ItemAttackerMob)owner).serverFollowersManager.addFollower("arachnidspiderling", (Mob)spiderling, FollowPosition.PYRAMID, "summonedmob", 1.0f, this::getMaxSpiderlings, null, false);
        Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(spiderling, owner.getLevel(), owner.x, owner.y);
        spiderling.updateDamage(spiderDamage);
        spiderling.setRemoveWhenNotInInventory(ItemRegistry.getItem("arachnidhelmet"), CheckSlotType.HELMET);
        owner.getLevel().entityManager.addMob(spiderling, spawnPoint.x, spawnPoint.y);
    }
}

