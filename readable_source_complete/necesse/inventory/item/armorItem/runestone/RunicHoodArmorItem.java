/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.runestone;

import java.awt.Color;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RunicHoodArmorItem
extends SetHelmetArmorItem {
    private GameTexture lightTexture;

    public RunicHoodArmorItem() {
        super(11, DamageTypeRegistry.RANGED, 750, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "runichood", "runicchestplate", "runicboots", "runicsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_CRIT_CHANCE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.PROJECTILE_VELOCITY, Float.valueOf(0.15f)));
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

