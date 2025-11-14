/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class BowProjectileToolItem
extends ProjectileToolItem {
    public int attackSpriteStretch = 8;
    public int moveDist = 25;

    public BowProjectileToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "rangedweapons");
        this.keyWords.add("bow");
        this.damageType = DamageTypeRegistry.RANGED;
        this.knockback.setBaseValue(25);
        this.attackRange.setBaseValue(500);
        this.itemAttackerPredictionDistanceOffset = -25.0f;
        this.tierOneEssencesUpgradeRequirement = "cryoessence";
        this.tierTwoEssencesUpgradeRequirement = "bloodessence";
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
        return ammoUser.getAvailableArrows("arrowammo");
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        this.addExtraBowTooltips(tooltips, item, perspective, blackboard);
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

    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "bowtip"));
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !(attackerMob instanceof AmmoUserMob) || this.getAvailableAmmo((AmmoUserMob)((Object)attackerMob)) > 0 ? null : "";
    }

    protected float getAmmoConsumeChance(ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob == null ? 1.0f : attackerMob.buffManager.getModifier(BuffModifiers.ARROW_USAGE).floatValue();
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        float chargePercent;
        if (item.getGndData().getBoolean("charging") && (chargePercent = item.getGndData().getFloat("chargePercent")) >= 0.0f) {
            chargePercent = Math.min(chargePercent, 1.0f);
            GameSprite attackSprite = this.getAttackSprite(item, player);
            int addedWidth = (int)(chargePercent * (float)this.attackSpriteStretch);
            attackSprite = new GameSprite(attackSprite, attackSprite.width + addedWidth, attackSprite.height);
            float chargedArm = item.getGndData().getFloat("chargedArm");
            if (chargedArm > 0.0f) {
                int addedArmWidth = (int)(chargedArm * (float)this.attackSpriteStretch);
                options.addedArmPosOffset(-addedArmWidth + this.attackSpriteStretch / 2, 0);
            } else {
                options.addedArmPosOffset(-addedWidth + this.attackSpriteStretch / 2, 0);
            }
            ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(attackSprite);
            itemSprite.itemRotatePoint(this.attackXOffset + addedWidth - this.attackSpriteStretch / 2, this.attackYOffset);
            if (itemColor != null) {
                itemSprite.itemColor(itemColor);
            }
            return itemSprite.itemEnd();
        }
        return super.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
    }

    public Item getArrowItem(Level level, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        if (attackerMob instanceof AmmoUserMob) {
            return ((AmmoUserMob)((Object)attackerMob)).getFirstAvailableArrow("arrowammo");
        }
        return ItemRegistry.getItem("stonearrow");
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        Item arrow = this.getArrowItem(level, attackerMob, seed, item);
        map.setShortUnsigned("arrowID", arrow == null ? 65535 : arrow.getID());
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int arrowID = mapContent.getShortUnsigned("arrowID", 65535);
        if (arrowID != 65535) {
            Item arrow = ItemRegistry.getItem(arrowID);
            if (arrow != null && arrow.type == Item.Type.ARROW) {
                boolean dropItem;
                boolean shouldFire;
                boolean consumeAmmo;
                GameRandom random = new GameRandom(seed + 5);
                float ammoConsumeChance = ((ArrowItem)arrow).getAmmoConsumeChance() * this.getAmmoConsumeChance(attackerMob, item);
                boolean bl = consumeAmmo = ammoConsumeChance >= 1.0f || ammoConsumeChance > 0.0f && random.getChance(ammoConsumeChance);
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob)((Object)attackerMob)).removeAmmo(arrow, 1, "arrowammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }
                if (shouldFire) {
                    this.fireProjectiles(level, x, y, attackerMob, item, seed, (ArrowItem)arrow, dropItem, mapContent);
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (arrow == null ? Integer.valueOf(arrowID) : arrow.getStringID()) + " as arrow.");
            }
        }
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (item.getGndData().getBoolean("charging")) {
            return;
        }
        if (level.isClient()) {
            SoundManager.playSound(this.getAttackSound(), attackerMob);
        }
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.bow).volume(0.5f);
    }

    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, GNDItemMap mapContent) {
        float velocity = arrow.modVelocity(this.getProjectileVelocity(item, attackerMob));
        int range = arrow.modRange(this.getAttackRange(item));
        GameDamage damage = arrow.modDamage(this.getAttackDamage(item));
        int knockback = arrow.modKnockback(this.getKnockback(item, attackerMob));
        float resilienceGain = this.getResilienceGain(item);
        return this.getProjectile(level, x, y, attackerMob, item, seed, arrow, consumeAmmo, velocity, range, damage, knockback, resilienceGain, mapContent);
    }

    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return arrow.getProjectile(owner.x, owner.y, x, y, velocity, range, damage, knockback, owner);
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean dropItem, GNDItemMap mapContent) {
        Projectile projectile = this.getProjectile(level, x, y, attackerMob, item, seed, arrow, dropItem, mapContent);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }

    @Override
    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        Item arrowItem = this.getArrowItem(level, attackerMob, seed, item);
        float velocity = (float)this.getProjectileVelocity(item, attackerMob) * (arrowItem == null ? 1.0f : ((ArrowItem)arrowItem).speedMod);
        return this.applyInaccuracy(attackerMob, item, this.getPredictedItemAttackerAttackPosition(attackerMob, target, velocity, this.itemAttackerPredictionDistanceOffset));
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "bow");
    }
}

