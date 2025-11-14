/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChestArmorItem
extends ArmorItem {
    public final String armsTextureName;
    public GameTexture rightArmsTexture;
    public GameTexture leftArmsTexture;
    public GameTexture frontLeftArmsTexture;
    public GameTexture frontRightArmsTexture;

    public ChestArmorItem(int armorValue, int enchantCost, String bodyTextureName, String armsTextureName, OneOfLootItems lootTableCategory) {
        super(ArmorItem.ArmorType.CHEST, armorValue, enchantCost, lootTableCategory, bodyTextureName);
        this.armsTextureName = armsTextureName;
        this.tierOneEssencesUpgradeRequirement = "cryoessence";
        this.tierTwoEssencesUpgradeRequirement = "bloodessence";
    }

    public ChestArmorItem(int armorValue, int enchantCost, Item.Rarity rarity, String bodyTextureName, String armsTextureName, OneOfLootItems lootTableCategory) {
        this(armorValue, enchantCost, bodyTextureName, armsTextureName, lootTableCategory);
        this.rarity = rarity;
    }

    @Override
    protected void loadArmorTexture() {
        super.loadArmorTexture();
        if (this.armsTextureName != null) {
            this.leftArmsTexture = GameTexture.fromFile("player/armor/" + this.armsTextureName + "_left");
            this.rightArmsTexture = GameTexture.fromFile("player/armor/" + this.armsTextureName + "_right");
            this.frontLeftArmsTexture = this.loadTextureIfExists("player/armor/" + this.armsTextureName + "_left_front");
            this.frontRightArmsTexture = this.loadTextureIfExists("player/armor/" + this.armsTextureName + "_right_front");
        }
    }

    public GameTexture getArmorLeftArmsTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        return this.leftArmsTexture;
    }

    public GameTexture getArmorRightArmsTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        return this.rightArmsTexture;
    }

    public GameTexture getFrontArmorRightArmsTexture(InventoryItem item, PlayerMob player) {
        return this.frontRightArmsTexture;
    }

    public GameTexture getFrontArmorLeftArmsTexture(InventoryItem item, PlayerMob player) {
        return this.frontLeftArmsTexture;
    }

    public GameSprite getAttackArmSprite(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        GameTexture armorTexture = this.getArmorTexture(item, level, player, headItem, chestItem, feetItem);
        return armorTexture == null ? null : new GameSprite(armorTexture, 0, 8, 32);
    }

    public DrawOptions getArmorLeftArmsDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture armorTexture = this.getArmorLeftArmsTexture(item, level, player, headItem, chestItem, feetItem);
        Color col = this.getDrawColor(item, player);
        if (armorTexture != null) {
            return armorTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
        }
        return () -> {};
    }

    public DrawOptions getArmorRightArmsDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture armorTexture = this.getArmorRightArmsTexture(item, level, player, headItem, chestItem, feetItem);
        Color col = this.getDrawColor(item, player);
        if (armorTexture != null) {
            return armorTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
        }
        return () -> {};
    }

    @Deprecated
    public final DrawOptions getArmorLeftArmsDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return this.getArmorLeftArmsDrawOptions(item, player == null ? null : player.getLevel(), player, null, null, null, spriteX, spriteY, spriteRes, drawX, drawY, 64, 64, mirrorX, mirrorY, light, alpha, mask);
    }

    @Deprecated
    public final DrawOptions getArmorRightArmsDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return this.getArmorRightArmsDrawOptions(item, player == null ? null : player.getLevel(), player, null, null, null, spriteX, spriteY, spriteRes, drawX, drawY, 64, 64, mirrorX, mirrorY, light, alpha, mask);
    }

    public DrawOptions getFrontArmorLeftArmsDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture backArmorTexture = this.getFrontArmorLeftArmsTexture(item, player);
        if (backArmorTexture != null) {
            Color col = this.getDrawColor(item, player);
            return backArmorTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
        }
        return null;
    }

    public DrawOptions getFrontArmorRightArmsDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture backArmorTexture = this.getFrontArmorRightArmsTexture(item, player);
        if (backArmorTexture != null) {
            Color col = this.getDrawColor(item, player);
            return backArmorTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
        }
        return null;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "bodyarmor");
    }
}

