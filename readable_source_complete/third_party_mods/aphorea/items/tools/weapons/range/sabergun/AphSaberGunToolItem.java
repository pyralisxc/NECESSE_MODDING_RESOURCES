/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.AttackHandler
 *  necesse.entity.mobs.itemAttacker.AmmoUserMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.gfx.gameFont.FontManager
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.ItemCategory
 *  necesse.inventory.item.ItemControllerInteract
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.inventory.item.ItemStatTip
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.inventory.item.LocalMessageDoubleItemStatTip
 *  necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem
 *  necesse.inventory.item.upgradeUtils.IntUpgradeValue
 *  necesse.inventory.lootTable.presets.GunWeaponsLootTable
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.range.sabergun;

import aphorea.items.tools.weapons.range.sabergun.logic.SaberGunAttackHandler;
import aphorea.items.tools.weapons.range.sabergun.logic.SaberGunDashAttackHandler;
import aphorea.registry.AphBuffs;
import aphorea.ui.AphCustomUIList;
import aphorea.ui.GunAttackUIManger;
import aphorea.utils.AphColors;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;
import necesse.level.maps.Level;

public abstract class AphSaberGunToolItem
extends ProjectileToolItem
implements ItemInteractAction {
    public IntUpgradeValue dashRange;

    public AphSaberGunToolItem(int enchantCost) {
        super(enchantCost, GunWeaponsLootTable.gunWeapons);
        this.setItemCategory(new String[]{"equipment", "weapons", "rangedweapons"});
        this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "rangedweapons"});
        this.setItemCategory(ItemCategory.craftingManager, new String[]{"equipment", "weapons", "rangedweapons"});
        this.damageType = DamageTypeRegistry.RANGED;
        this.knockback.setBaseValue(25);
        this.attackRange.setBaseValue(500);
        this.enchantCost.setUpgradedValue(1.0f, 2100);
        this.attackAnimTime.setBaseValue(500);
        this.velocity.setBaseValue(400);
        this.dashRange = new IntUpgradeValue(200, 0.0f);
        this.dashRange.setBaseValue(200);
        this.keyWords.add("saber");
        this.keyWords.add("sabergun");
        this.itemAttackerProjectileCanHitWidth = 28.0f;
        this.attackXOffset = 16;
        this.attackYOffset = 10;
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
        drawOptions.pointRotation(attackDirX, attackDirY, 45.0f);
    }

    public int getAvailableAmmo(AmmoUserMob ammoUser) {
        return ammoUser == null ? 0 : ammoUser.getAvailableAmmo(new Item[]{ItemRegistry.getItem((String)"simplebullet")}, "bulletammo");
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        int ammoAmount = this.getAvailableAmmo((AmmoUserMob)perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"ammotip", (String)"value", (Object)ammoAmount));
        this.addLeftClickTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"saberdash"));
        return tooltips;
    }

    public void addLeftClickTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        if (this.getBaseArmorPenPercent() != 0.0f) {
            this.addAttackArmorPenTip(list, currentItem, lastItem, perspective);
        }
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, (Mob)perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
    }

    public void addAttackArmorPenTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob attackerMob) {
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "attackarmorpentip", "value", (double)(this.getBaseArmorPenPercent() * 100.0f), 0);
        if (lastItem != null) {
            int lastMaxSpeed = Math.max(this.getAttackAnimTime(lastItem, attackerMob), this.getAttackCooldownTime(lastItem, attackerMob));
            tip.setCompareValue(this.toAttacksPerSecond(lastMaxSpeed));
        }
        list.add(200, (ItemStatTip)tip);
    }

    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob instanceof AmmoUserMob && this.getAvailableAmmo((AmmoUserMob)attackerMob) > 0 ? null : "";
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int animTime = this.getAttackAnimTime(item, attackerMob);
        attackerMob.startAttackHandler((AttackHandler)new SaberGunAttackHandler(attackerMob, slot, item, this, animTime, seed));
        return item;
    }

    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    public abstract float getBaseArmorPenPercent();

    public float getArmorPenPercent(Level level, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.getBaseArmorPenPercent();
    }

    public abstract Projectile getProjectile(Level var1, int var2, int var3, ItemAttackerMob var4, InventoryItem var5);

    public abstract int getProjectilesNumber(InventoryItem var1);

    public abstract float getProjectilesMaxSpread(InventoryItem var1);

    public abstract float getDashDamageMultiplier(InventoryItem var1);

    public abstract void doAttack(Level var1, int var2, int var3, ItemAttackerMob var4, InventoryItem var5, int var6);

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"sabergun");
    }

    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.canDash(attackerMob);
    }

    public boolean canDash(ItemAttackerMob attackerMob) {
        return !attackerMob.isRiding() && !attackerMob.buffManager.hasBuff(AphBuffs.SABER_DASH_COOLDOWN);
    }

    public int getLevelInteractCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 0;
    }

    public boolean getConstantInteract(InventoryItem item) {
        return true;
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        int animTime = (int)((float)this.getAttackAnimTime(item, attackerMob));
        mapContent.setBoolean("charging", true);
        attackerMob.startAttackHandler(new SaberGunDashAttackHandler(attackerMob, slot, item, this, animTime, AphColors.lighter_gray, seed).startFromInteract());
        return item;
    }

    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y){

            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient() && level.getClient().getPlayer().getUniqueID() == attackerMob.getUniqueID()) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
            AphCustomUIList.gunAttack.chargePercent = item.getGndData().getFloat("chargePercent");
        }
    }

    public static float spreadPercent(float chargePercent) {
        return Math.abs(GunAttackUIManger.barPercent(chargePercent));
    }
}

