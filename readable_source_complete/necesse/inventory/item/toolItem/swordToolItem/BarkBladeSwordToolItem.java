/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.BarkBladeGreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.BarkBladeLeafProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class BarkBladeSwordToolItem
extends SwordToolItem
implements ItemInteractAction {
    public final GreatswordToolItem greatswordItem = new GreatswordToolItem(1550, GreatswordWeaponsLootTable.greatswordWeapons, this.getChargeLevels());
    public FloatUpgradeValue greatswordDamage = new FloatUpgradeValue();
    public int greatswordRange = 130;
    public GameTexture greatswordTexture;

    public BarkBladeSwordToolItem() {
        super(1550, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(60.0f).setUpgradedValue(1.0f, 93.33336f);
        this.greatswordDamage.setBaseValue(160.0f).setUpgradedValue(1.0f, 186.66672f);
        this.attackRange.setBaseValue(78);
        this.knockback.setBaseValue(100);
        this.resilienceGain.setBaseValue(1.0f);
        this.attackXOffset = 16;
        this.attackYOffset = 16;
        this.canBeUsedForRaids = false;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "barkbladetip1"), 400);
        tooltips.add(Localization.translate("itemtooltip", "barkbladetip2"), 400);
        tooltips.add(Localization.translate("itemtooltip", "barkbladetip3"), 400);
        return tooltips;
    }

    @Override
    protected void loadHoldTextures() {
        this.greatswordTexture = GameTexture.fromFile("player/weapons/" + this.getStringID() + "_greatsword");
        super.loadHoldTextures();
    }

    public GreatswordChargeLevel[] getChargeLevels() {
        return new GreatswordChargeLevel[]{new GreatswordChargeLevel(500, 1.0f, new Color(200, 200, 200)), new GreatswordChargeLevel(600, 1.5f, new Color(200, 200, 100)), new GreatswordChargeLevel(700, 2.0f, new Color(200, 100, 100))};
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob.buffManager.hasBuff(BuffRegistry.BARKBLADE_ENHANCED)) {
            float velocity = 140.0f;
            float finalVelocity = Math.round(this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue).floatValue() * velocity * attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue());
            for (int i = -1; i <= 1; ++i) {
                BarkBladeLeafProjectile projectile = new BarkBladeLeafProjectile(level, attackerMob.x, attackerMob.y, x, y, finalVelocity, 600, new GameDamage(this.getAttackDamage((InventoryItem)item).damage / 6.0f), attackerMob);
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item) / 3.0f));
                projectile.resetUniqueID(new GameRandom(seed));
                attackerMob.addAndSendAttackerProjectile(projectile, 20, 10 * i);
            }
        }
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public GameDamage getFlatAttackDamage(InventoryItem item) {
        if (item.getGndData().getBoolean("charged")) {
            return new GameDamage(this.greatswordDamage.getValue(this.getUpgradeTier(item)).floatValue());
        }
        return super.getFlatAttackDamage(item);
    }

    @Override
    public int getFlatAttackRange(InventoryItem item) {
        if (item.getGndData().getBoolean("charged")) {
            return this.greatswordRange;
        }
        return super.getFlatAttackRange(item);
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        if (item.getGndData().getBoolean("charging") || item.getGndData().getBoolean("charged")) {
            return new GameSprite(this.greatswordTexture, 90);
        }
        return super.getAttackSprite(item, player);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (!item.getGndData().getBoolean("charged")) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        } else {
            SoundManager.playSound(GameResources.regularGreatSwords1, (SoundEffect)SoundEffect.effect(x, y).volume(0.6f).pitch(GameRandom.globalRandom.getFloatBetween(0.97f, 1.03f)));
        }
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new BarkBladeGreatswordAttackHandler(attackerMob, slot, item, this, seed, x, y, this.getChargeLevels()).startFromInteract());
        return item;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return super.getAttackSound();
    }
}

