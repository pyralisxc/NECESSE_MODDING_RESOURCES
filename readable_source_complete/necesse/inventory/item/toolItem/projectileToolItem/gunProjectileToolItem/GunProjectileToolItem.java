/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.HandGunBulletProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class GunProjectileToolItem
extends ProjectileToolItem {
    public static LinkedHashSet<String> NORMAL_AMMO_TYPES = new LinkedHashSet<String>(Arrays.asList("simplebullet", "bouncingbullet", "voidbullet", "frostbullet", "crystalbullet"));
    public LinkedHashSet<String> ammoTypes;
    public int moveDist;
    public boolean controlledRange;
    public int controlledMinRange;
    public int controlledInaccuracy;
    public float ammoConsumeChance;

    public GunProjectileToolItem(String ammoStringID, int enchantCost, OneOfLootItems lootTableCategory) {
        this(new LinkedHashSet<String>(Collections.singletonList(ammoStringID)), enchantCost, lootTableCategory);
    }

    public GunProjectileToolItem(LinkedHashSet<String> ammoTypes, int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "rangedweapons");
        this.keyWords.add("gun");
        this.damageType = DamageTypeRegistry.RANGED;
        this.knockback.setBaseValue(50);
        this.velocity.setBaseValue(400);
        this.moveDist = 20;
        this.ammoConsumeChance = 1.0f;
        this.ammoTypes = ammoTypes;
        this.tierOneEssencesUpgradeRequirement = "shadowessence";
        this.tierTwoEssencesUpgradeRequirement = "spideressence";
    }

    @Override
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        if (inInventory) {
            int ammoAmount = this.getAvailableAmmo(perspective);
            if (ammoAmount > 999) {
                ammoAmount = 999;
            }
            String amountString = String.valueOf(ammoAmount);
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString(x + 28 - width, y + 16, amountString, tipFontOptions);
        }
    }

    public int getAvailableAmmo(AmmoUserMob ammoUser) {
        if (ammoUser == null) {
            return 0;
        }
        return ammoUser.getAvailableAmmo(this.ammoItems(), "bulletammo");
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        this.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        this.addAmmoTooltips(tooltips, item);
        int ammoAmount = this.getAvailableAmmo(perspective);
        tooltips.add(Localization.translate("itemtooltip", "ammotip", "value", (Object)ammoAmount));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "guntip"));
    }

    @Override
    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        if (this.controlledRange) {
            int range = this.getAttackRange(item);
            return new Point((int)(player.x + aimDirX * (float)range), (int)(player.y + aimDirY * (float)range));
        }
        return super.getControllerAttackLevelPos(level, aimDirX, aimDirY, player, item);
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !(attackerMob instanceof AmmoUserMob) || this.getAvailableAmmo((AmmoUserMob)((Object)attackerMob)) > 0 ? null : "";
    }

    public Item getBulletItem(Level level, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        if (attackerMob instanceof AmmoUserMob) {
            return ((AmmoUserMob)((Object)attackerMob)).getFirstAvailableAmmo(this.ammoItems(), "bulletammo");
        }
        return this.ammoTypes.isEmpty() ? null : ItemRegistry.getItem((String)this.ammoTypes.iterator().next());
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        Item bullet = this.getBulletItem(level, attackerMob, seed, item);
        map.setShortUnsigned("bulletID", bullet == null ? 65535 : bullet.getID());
    }

    protected Item[] ammoItems() {
        return (Item[])this.ammoTypes.stream().map(ItemRegistry::getItem).toArray(Item[]::new);
    }

    protected float getAmmoConsumeChance(ItemAttackerMob attackerMob, InventoryItem item) {
        float playerMod = attackerMob == null ? 1.0f : attackerMob.buffManager.getModifier(BuffModifiers.BULLET_USAGE).floatValue();
        return this.ammoConsumeChance * playerMod;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int bulletID = mapContent.getShortUnsigned("bulletID", 65535);
        if (bulletID != 65535) {
            Item bullet = ItemRegistry.getItem(bulletID);
            if (bullet != null && bullet.type == Item.Type.BULLET) {
                boolean dropItem;
                boolean shouldFire;
                boolean consumeAmmo;
                GameRandom random = new GameRandom(seed + 5);
                float ammoConsumeChance = ((BulletItem)bullet).getAmmoConsumeChance() * this.getAmmoConsumeChance(attackerMob, item);
                boolean bl = consumeAmmo = ammoConsumeChance >= 1.0f || ammoConsumeChance > 0.0f && random.getChance(ammoConsumeChance);
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob)((Object)attackerMob)).removeAmmo(bullet, 1, "bulletammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }
                if (shouldFire) {
                    this.fireProjectiles(level, x, y, attackerMob, item, seed, (BulletItem)bullet, dropItem, mapContent);
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (bullet == null ? Integer.valueOf(bulletID) : bullet.getStringID()) + " as bullet.");
            }
        }
        return item;
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(new GameRandom(seed + 10), attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)attackerMob.getDistance(x, y);
        } else {
            range = this.getAttackRange(item);
        }
        Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, x, y, range, attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }

    public Projectile getProjectile(InventoryItem item, BulletItem bulletItem, float x, float y, float targetX, float targetY, int range, ItemAttackerMob attackerMob) {
        float velocity = bulletItem.modVelocity(this.getProjectileVelocity(item, attackerMob));
        range = bulletItem.modRange(range);
        GameDamage damage = bulletItem.modDamage(this.getAttackDamage(item));
        int knockback = bulletItem.modKnockback(this.getKnockback(item, attackerMob));
        if (bulletItem.overrideProjectile()) {
            return bulletItem.getProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, attackerMob);
        }
        return this.getNormalProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, attackerMob);
    }

    public Projectile getNormalProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage toolItemDamage, int knockback, ItemAttackerMob attackerMob) {
        return new HandGunBulletProjectile(x, y, targetX, targetY, velocity, range, toolItemDamage, knockback, attackerMob);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "gun");
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.handgun);
    }
}

