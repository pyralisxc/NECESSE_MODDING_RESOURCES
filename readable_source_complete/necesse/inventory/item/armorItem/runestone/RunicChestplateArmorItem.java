/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.runestone;

import java.awt.Color;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RunicChestplateArmorItem
extends ChestArmorItem {
    public GameTexture lightTexture;

    public RunicChestplateArmorItem() {
        super(11, 750, Item.Rarity.UNCOMMON, "runicchestplate", "runicarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.05f)));
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

