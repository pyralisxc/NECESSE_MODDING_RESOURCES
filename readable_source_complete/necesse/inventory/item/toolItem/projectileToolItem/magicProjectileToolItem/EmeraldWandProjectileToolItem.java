/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.EmeraldWandAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.EmeraldWandProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class EmeraldWandProjectileToolItem
extends MagicProjectileToolItem {
    public EmeraldWandProjectileToolItem() {
        super(1900, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(100);
        this.attackDamage.setBaseValue(45.0f).setUpgradedValue(1.0f, 77.00003f);
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(500);
        this.attackXOffset = 16;
        this.attackYOffset = 8;
        this.manaCost.setBaseValue(1.0f).setUpgradedValue(1.0f, 1.0f);
        this.resilienceGain.setBaseValue(0.75f);
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (attackDirX < 0.0f) {
            drawOptions.rotation(-GameMath.getAngle(new Point2D.Float(attackDirX, attackDirY)) + 45.0f + 180.0f);
        } else {
            drawOptions.rotation(GameMath.getAngle(new Point2D.Float(attackDirX, attackDirY)) + 45.0f);
        }
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        if (item.getGndData().getBoolean("charging")) {
            return 2000;
        }
        return super.getAttackAnimTime(item, attackerMob);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "emeraldwandtip"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new EmeraldWandAttackHandler(attackerMob, slot, item, this, seed, x, y));
        if (attackerMob.isClient()) {
            SoundManager.playSound(new SoundSettings(GameResources.swing1).volume(0.4f).pitchVariance(0.1f), attackerMob);
        }
        return item;
    }

    public void fireProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        float distance = GameMath.diamondDistance(attackerMob.x, attackerMob.y, x, y);
        float t = 32.0f / distance;
        float projectileX = (1.0f - t) * attackerMob.x + t * (float)x;
        float projectileY = (1.0f - t) * attackerMob.y + t * (float)y;
        GameRandom random = new GameRandom(seed);
        GameRandom spreadRandom = new GameRandom(seed + 10);
        EmeraldWandProjectile projectile = new EmeraldWandProjectile(projectileX, projectileY, x, y, random.getIntBetween(500, 250), 500, this.getAttackDamage(item), 0, attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.getUniqueID(random);
        projectile.setAngle(projectile.getAngle() + spreadRandom.getFloatOffset(0.0f, 15.0f));
        attackerMob.addAndSendAttackerProjectile(projectile);
        this.consumeMana(attackerMob, item);
    }

    @Override
    protected SoundSettings getSwingSound() {
        return null;
    }
}

