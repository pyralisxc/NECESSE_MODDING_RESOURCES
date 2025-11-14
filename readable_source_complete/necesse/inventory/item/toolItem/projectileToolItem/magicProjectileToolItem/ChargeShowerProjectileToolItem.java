/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ChargeShowerLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.ChargeShowerAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.level.maps.Level;

public class ChargeShowerProjectileToolItem
extends MagicProjectileToolItem {
    public ChargeShowerProjectileToolItem() {
        super(1900, null);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(4000);
        this.attackDamage.setBaseValue(500.0f).setUpgradedValue(1.0f, 510.0f);
        this.knockback.setBaseValue(10);
        this.velocity.setBaseValue(120);
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(300);
        this.attackXOffset = 20;
        this.attackYOffset = 20;
        this.manaCost.setBaseValue(0.0f);
        this.resilienceGain.setBaseValue(0.75f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.canBeUsedForRaids = false;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("NOT_OBTAINABLE: Charge Shower");
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        if (item.getGndData().getBoolean("charging")) {
            return 2000;
        }
        return super.getAttackAnimTime(item, attackerMob);
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        return false;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        String superError = super.canAttack(level, x, y, attackerMob, item);
        if (superError != null) {
            return superError;
        }
        return attackerMob.isOnGenericCooldown("chargeShower") ? "" : null;
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        return options;
    }

    @Override
    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        return this.applyInaccuracy(attackerMob, item, new Point(target.getX(), target.getY()));
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int fullChargeTime = this.getAttackAnimTime(item, attackerMob);
        int range = this.getAttackRange(item);
        long startTime = level.getTime();
        ChargeShowerLevelEvent event = new ChargeShowerLevelEvent(attackerMob, seed, startTime, fullChargeTime);
        attackerMob.addAndSendAttackerLevelEvent(event);
        attackerMob.startAttackHandler(new ChargeShowerAttackHandler(attackerMob, slot, 75, 50.0f, x, y, seed, event, range));
        return item;
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return (int)((float)this.getAttackRange(item) * 0.8f);
    }
}

