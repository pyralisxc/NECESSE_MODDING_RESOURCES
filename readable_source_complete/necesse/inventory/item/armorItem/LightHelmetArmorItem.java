/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem;

import java.awt.Color;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.HelmetArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LightHelmetArmorItem
extends HelmetArmorItem {
    public GameTexture lightTexture;

    public LightHelmetArmorItem(int armorValue, DamageType damageClass, int enchantCost, Item.Rarity itemRarity, String textureName) {
        super(armorValue, damageClass, enchantCost, itemRarity, textureName, null);
    }

    @Override
    protected void loadArmorTexture() {
        super.loadArmorTexture();
        this.lightTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_light");
    }

    @Override
    public DrawOptions getArmorDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        DrawOptionsList options = new DrawOptionsList();
        options.add(super.getArmorDrawOptions(item, level, player, headItem, chestItem, feetItem, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask));
        Color col = this.getDrawColor(item, player);
        options.add(this.lightTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light.minLevelCopy(150.0f)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY));
        return options;
    }
}

