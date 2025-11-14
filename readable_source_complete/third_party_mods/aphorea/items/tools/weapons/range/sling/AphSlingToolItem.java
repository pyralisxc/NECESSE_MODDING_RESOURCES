/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.AttackHandler
 *  necesse.entity.mobs.itemAttacker.AmmoConsumed
 *  necesse.entity.mobs.itemAttacker.AmmoUserMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.gfx.gameFont.FontManager
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.ItemCategory
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem
 *  necesse.inventory.lootTable.presets.BowWeaponsLootTable
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.range.sling;

import aphorea.items.tools.weapons.range.sling.logic.SlingAttackHandler;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;
import necesse.level.maps.Level;

public abstract class AphSlingToolItem
extends ProjectileToolItem {
    public AphSlingToolItem(int enchantCost) {
        super(enchantCost, BowWeaponsLootTable.bowWeapons);
        this.setItemCategory(new String[]{"equipment", "weapons", "rangedweapons"});
        this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "rangedweapons"});
        this.setItemCategory(ItemCategory.craftingManager, new String[]{"equipment", "weapons", "rangedweapons"});
        this.damageType = DamageTypeRegistry.RANGED;
        this.knockback.setBaseValue(25);
        this.attackRange.setBaseValue(500);
        this.enchantCost.setUpgradedValue(1.0f, 2100);
        this.keyWords.add("sling");
        this.itemAttackerProjectileCanHitWidth = 28.0f;
    }

    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        if (inInventory) {
            int ammoAmount = this.getAvailableAmmo((AmmoUserMob)perspective);
            if (ammoAmount > 999) {
                ammoAmount = 999;
            }
            String amountString = String.valueOf(ammoAmount);
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString((float)(x + 28 - width), (float)(y + 16), amountString, tipFontOptions);
        }
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(0.0f, 0.0f, -item.getGndData().getFloat("showAngle"));
    }

    public int getAvailableAmmo(AmmoUserMob ammoUser) {
        return ammoUser == null ? 0 : ammoUser.getAvailableAmmo(new Item[]{ItemRegistry.getItem((String)"stone")}, "stone");
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        int ammoAmount = this.getAvailableAmmo((AmmoUserMob)perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"ammotip", (String)"value", (Object)ammoAmount));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"sling"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"sling2"));
        return tooltips;
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, (Mob)perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
    }

    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob instanceof AmmoUserMob && this.getAvailableAmmo((AmmoUserMob)attackerMob) > 0 ? null : "";
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int animTime = this.getAttackAnimTime(item, attackerMob);
        attackerMob.startAttackHandler((AttackHandler)new SlingAttackHandler(attackerMob, slot, item, this, animTime, seed));
        return item;
    }

    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    public abstract Projectile getProjectile(Level var1, int var2, int var3, ItemAttackerMob var4, InventoryItem var5);

    public void doAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        boolean shouldFire;
        if (attackerMob instanceof AmmoUserMob) {
            AmmoConsumed consumed = ((AmmoUserMob)attackerMob).removeAmmo(ItemRegistry.getItem((String)"stone"), 1, "stone");
            shouldFire = consumed.amount >= 1;
        } else {
            shouldFire = true;
        }
        if (shouldFire) {
            Projectile projectile = this.getProjectile(level, x, y, attackerMob, item);
            projectile.resetUniqueID(new GameRandom((long)seed));
            attackerMob.addAndSendAttackerProjectile(projectile, 20);
        }
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"sling");
    }
}

