/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.crystal;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.IncursionFeetArmorLootTable;
import necesse.level.maps.Level;

public class CrystalBoots
extends BootsArmorItem {
    public GameTexture amethystTexture;
    public GameTexture sapphireTexture;
    public GameTexture emeraldTexture;
    public GameTexture rubyTexture;
    public FloatUpgradeValue speed = new FloatUpgradeValue().setBaseValue(0.25f).setUpgradedValue(1.0f, 0.25f);

    public CrystalBoots() {
        super(17, 1900, Item.Rarity.EPIC, "crystalboots", IncursionFeetArmorLootTable.incursionFeetArmor);
    }

    @Override
    protected void loadArmorTexture() {
        super.loadArmorTexture();
        this.amethystTexture = GameTexture.fromFile("player/armor/amethystboots");
        this.sapphireTexture = GameTexture.fromFile("player/armor/sapphireboots");
        this.emeraldTexture = GameTexture.fromFile("player/armor/emeraldboots");
        this.rubyTexture = GameTexture.fromFile("player/armor/rubyboots");
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 15), new ModifierValue<Float>(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(item))));
    }

    @Override
    public GameTexture getArmorTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        if (headItem != null) {
            if (headItem.item.getStringID().equals("amethysthelmet")) {
                return this.amethystTexture;
            }
            if (headItem.item.getStringID().equals("sapphireeyepatch")) {
                return this.sapphireTexture;
            }
            if (headItem.item.getStringID().equals("emeraldmask")) {
                return this.emeraldTexture;
            }
            if (headItem.item.getStringID().equals("rubycrown")) {
                return this.rubyTexture;
            }
        }
        return super.getArmorTexture(item, level, player, headItem, chestItem, feetItem);
    }
}

