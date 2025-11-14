/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseBeamAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class DragonLanceProjectileToolItem
extends MagicProjectileToolItem {
    public DragonLanceProjectileToolItem() {
        super(1800, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(2000);
        this.attackDamage.setBaseValue(80.0f).setUpgradedValue(1.0f, 126.00004f);
        this.knockback.setBaseValue(10);
        this.velocity.setBaseValue(120);
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(400);
        this.attackXOffset = 20;
        this.attackYOffset = 20;
        this.manaCost.setBaseValue(10.0f).setUpgradedValue(1.0f, 10.0f);
        this.resilienceGain.setBaseValue(1.0f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.canBeUsedForRaids = false;
    }

    @Override
    public float getFinalAttackMovementMod(InventoryItem item, ItemAttackerMob attackerMob) {
        return 0.0f;
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
    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        return this.applyInaccuracy(attackerMob, item, new Point(target.getX(), target.getY()));
    }

    @Override
    protected SoundSettings getSwingSound() {
        return new SoundSettings(GameResources.magicbolt1).basePitch(1.4f).volume(0.3f);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.dragonLance).basePitch(0.9f).volume(0.05f);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "dragonlancetip"));
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
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return (int)((float)this.getAttackRange(item) * 0.8f);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        float enchantmentSpeedModifier = this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.ATTACK_SPEED, (Float)ToolItemModifiers.ATTACK_SPEED.defaultBuffValue).floatValue();
        MouseBeamLevelEvent event = new MouseBeamLevelEvent(attackerMob, x, y, seed, 50.0f, this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), null, 250, enchantmentSpeedModifier, 0, this.getResilienceGain(item), new Color(212, 75, 41));
        attackerMob.addAndSendAttackerLevelEvent(event);
        float startManaCost = this.getManaCost(item);
        if (startManaCost > 0.0f) {
            this.consumeMana(startManaCost, attackerMob);
        }
        attackerMob.startAttackHandler(new MouseBeamAttackHandler(attackerMob, slot, 75, seed, event).setManaCostPerSecond(this.getManaCost(item)));
        return item;
    }
}

