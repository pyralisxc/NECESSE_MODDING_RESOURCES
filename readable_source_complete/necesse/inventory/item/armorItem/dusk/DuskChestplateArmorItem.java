/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.dusk;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.IncursionBodyArmorLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DuskChestplateArmorItem
extends ChestArmorItem {
    public GameTexture brokenTexture;
    public GameTexture brokenLeftArmTexture;
    public GameTexture brokenRightArmTexture;

    public DuskChestplateArmorItem() {
        super(29, 1900, Item.Rarity.EPIC, "duskchestplate", "duskarms", IncursionBodyArmorLootTable.incursionBodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Integer>(BuffModifiers.MAX_SUMMONS, 2), new ModifierValue<Float>(BuffModifiers.MAGIC_CRIT_CHANCE, Float.valueOf(0.3f)), new ModifierValue<Float>(BuffModifiers.SUMMON_CRIT_CHANCE, Float.valueOf(0.3f)));
    }

    @Override
    protected void loadArmorTexture() {
        super.loadArmorTexture();
        this.brokenTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_broken");
        this.brokenLeftArmTexture = GameTexture.fromFile("player/armor/duskarms_left_broken");
        this.brokenRightArmTexture = GameTexture.fromFile("player/armor/duskarms_right_broken");
    }

    @Override
    public GameTexture getArmorLeftArmsTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        if (level != null && !level.getWorldEntity().isNight()) {
            return this.brokenLeftArmTexture;
        }
        return super.getArmorLeftArmsTexture(item, level, player, headItem, chestItem, feetItem);
    }

    @Override
    public GameTexture getArmorRightArmsTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        if (level != null && !level.getWorldEntity().isNight()) {
            return this.brokenRightArmTexture;
        }
        return super.getArmorRightArmsTexture(item, level, player, headItem, chestItem, feetItem);
    }

    @Override
    public GameSprite getAttackArmSprite(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        if (level != null && !level.getWorldEntity().isNight()) {
            return this.brokenTexture == null ? null : new GameSprite(this.brokenTexture, 0, 8, 32);
        }
        return super.getAttackArmSprite(item, level, player, headItem, chestItem, feetItem);
    }

    @Override
    public DrawOptions getArmorDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        if (level != null && !level.getWorldEntity().isNight()) {
            return this.brokenTexture.initDraw().sprite(spriteX, spriteY, spriteRes).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY).light(light);
        }
        return super.getArmorDrawOptions(item, level, player, headItem, chestItem, feetItem, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask);
    }
}

