/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ReturnLifeOnHitLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseBeamAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.BloodGrimoireRightClickProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.IncursionMagicWeaponsLootTable;
import necesse.level.maps.Level;

public class BloodGrimoireProjectileToolItem
extends MagicProjectileToolItem
implements ItemInteractAction {
    public BloodGrimoireProjectileToolItem() {
        super(1900, IncursionMagicWeaponsLootTable.incursionMagicWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(2000);
        this.attackDamage.setBaseValue(84.0f).setUpgradedValue(1.0f, 126.00004f);
        this.knockback.setBaseValue(10);
        this.velocity.setBaseValue(120);
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(225);
        this.attackXOffset = 20;
        this.attackYOffset = 20;
        this.manaCost.setBaseValue(0.0f);
        this.lifeCost.setBaseValue(15);
        this.lifeSteal.setBaseValue(3);
        this.resilienceGain.setBaseValue(0.75f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.canBeUsedForRaids = false;
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
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.bloodGrimoire).volume(0.1f);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "bloodgrimoiretip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "bloodgrimoiresecondarytip"), 400);
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    @Override
    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        return this.applyInaccuracy(attackerMob, item, new Point(target.getX(), target.getY()));
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        int lifeCostPerSecond;
        if (!attackerMob.isPlayer && (lifeCostPerSecond = this.getFlatLifeCost(item)) > 0 && attackerMob.getHealth() < lifeCostPerSecond * 5) {
            return "";
        }
        return super.canAttack(level, x, y, attackerMob, item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        float enchantmentSpeedModifier = this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.ATTACK_SPEED, (Float)ToolItemModifiers.ATTACK_SPEED.defaultBuffValue).floatValue();
        ReturnLifeOnHitLevelEvent event = new ReturnLifeOnHitLevelEvent(attackerMob, x, y, seed, 50.0f, this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), null, 500, enchantmentSpeedModifier, 0, this.getResilienceGain(item), new Color(147, 16, 45), this.getLifeSteal(item));
        attackerMob.addAndSendAttackerLevelEvent(event);
        attackerMob.startAttackHandler(new MouseBeamAttackHandler(attackerMob, slot, 75, seed, event).setLifeCostPerSecond(this.getFlatLifeCost(item)));
        return item;
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return (int)((float)this.getAttackRange(item) * 0.8f);
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.buffManager.hasBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_COOLDOWN);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_COOLDOWN, (Mob)attackerMob, 5.0f, null), false);
        BloodGrimoireRightClickProjectile projectile = new BloodGrimoireRightClickProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, 80.0f, this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob));
        projectile.getUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile);
        return item;
    }

    @Override
    public int getLevelInteractAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 400;
    }

    @Override
    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return perspective.buffManager.getBuffDurationLeftSeconds(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_COOLDOWN) / 5.0f;
    }

    @Override
    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y){

            @Override
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            @Override
            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }
}

