/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.entity.mobs.MaskShaderOptions
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.armorItem.ArmorItem$FacialFeatureDrawMode
 *  necesse.inventory.item.armorItem.ArmorModifiers
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.items.armor.Spinel;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import aphorea.registry.AphModifiers;
import java.awt.Color;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpinelHat
extends AphSetHelmetArmorItem {
    public SpinelHat() {
        super(4, DamageTypeRegistry.MAGIC, 1300, Item.Rarity.UNCOMMON, "spinelhat", "spinelchestplate", "spinelboots", "spinelhatsetbonus");
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
    }

    public DrawOptions getArmorDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture armorTexture = this.getArmorTexture(item, level, player, headItem, chestItem, feetItem);
        Color col = this.getDrawColor(item, player);
        return armorTexture.initDraw().sprite(spriteX, spriteY, spriteRes + 32).colorLight(col, light).alpha(alpha).size(width + 32, height + 32).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX - 16, drawY - 16);
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING, (Object)Float.valueOf(0.1f))});
    }
}

