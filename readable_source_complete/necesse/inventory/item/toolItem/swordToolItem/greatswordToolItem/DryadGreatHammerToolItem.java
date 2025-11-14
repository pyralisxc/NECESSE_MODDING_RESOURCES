/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DryadSpiritFollowingMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class DryadGreatHammerToolItem
extends GreatswordToolItem {
    final int dryadHauntedStacksOnHit = 6;

    public DryadGreatHammerToolItem() {
        super(1550, GreatswordWeaponsLootTable.greatswordWeapons, DryadGreatHammerToolItem.getThreeChargeLevels(500, 600, 700));
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(160.0f).setUpgradedValue(1.0f, 198.33339f);
        this.attackRange.setBaseValue(110);
        this.knockback.setBaseValue(150);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "dryadgreathammertip"), 400);
        tooltips.add(new SpacerGameTooltip(5));
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "dryadhauntweapontip", "value", (Object)6), new Color(30, 177, 143), 400));
        return tooltips;
    }

    public static GreatswordChargeLevel[] getThreeChargeLevels(int level1Time, int level2Time, int level3Time) {
        return new GreatswordChargeLevel[]{new GreatswordChargeLevel(level1Time, 1.0f, new Color(200, 200, 200)), new GreatswordChargeLevel(level2Time, 2.0f, new Color(30, 177, 143)), new GreatswordChargeLevel(level3Time, 1.0f, new Color(200, 200, 200))};
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        Buff dryadHaunted = BuffRegistry.Debuffs.DRYAD_HAUNTED;
        ActiveBuff ab = new ActiveBuff(dryadHaunted, target, 10000, (Attacker)attacker);
        ab.setStacks(6, 10000, attacker);
        target.buffManager.addBuff(ab, true);
        if (target.buffManager.getStacks(dryadHaunted) >= 10) {
            target.buffManager.removeBuff(dryadHaunted, true);
            DryadGreatHammerToolItem.spawnDryadSpirit(attacker);
        }
    }

    public static void spawnDryadSpirit(Mob owner) {
        if (owner != null && owner.isServer()) {
            int maxSummons = 5;
            DryadSpiritFollowingMob summonedMob = (DryadSpiritFollowingMob)MobRegistry.getMob("dryadspirit", owner.getLevel());
            ((ItemAttackerMob)owner).serverFollowersManager.addFollower("summonedmobtemp", (Mob)summonedMob, FollowPosition.FLYING_CIRCLE_FAST, "summonedmob", 1.0f, p -> maxSummons, null, false);
            Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(summonedMob, owner.getLevel(), owner.x, owner.y);
            owner.getLevel().entityManager.addMob(summonedMob, spawnPoint.x, spawnPoint.y);
        }
    }

    @Override
    protected SoundSettings getGreatswordSwingSound1() {
        return new SoundSettings(GameResources.dryadGreatHammer1).volume(0.2f);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound2() {
        return new SoundSettings(GameResources.dryadGreatHammer2).volume(0.3f);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound3() {
        return this.getGreatswordSwingSound1();
    }
}

