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
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.IncursionBodyArmorLootTable;
import necesse.level.maps.Level;

public class CrystalChestplate
extends ChestArmorItem {
    public GameTexture amethystTexture;
    public GameTexture sapphireTexture;
    public GameTexture emeraldTexture;
    public GameTexture rubyTexture;
    public GameTexture amethystLeftArmTexture;
    public GameTexture amethystRightArmTexture;
    public GameTexture sapphireLeftArmTexture;
    public GameTexture sapphireRightArmTexture;
    public GameTexture emeraldLeftArmTexture;
    public GameTexture emeraldRightArmTexture;
    public GameTexture rubyLeftArmTexture;
    public GameTexture rubyRightArmTexture;

    public CrystalChestplate() {
        super(29, 1900, Item.Rarity.EPIC, "crystalchestplate", "crystalarms", IncursionBodyArmorLootTable.incursionBodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.15f)), new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 35), new ModifierValue<Float>(BuffModifiers.RESILIENCE_REGEN_FLAT, Float.valueOf(1.0f)));
    }

    @Override
    protected void loadArmorTexture() {
        super.loadArmorTexture();
        this.amethystTexture = GameTexture.fromFile("player/armor/amethystchestplate");
        this.amethystLeftArmTexture = GameTexture.fromFile("player/armor/amethystarms_left");
        this.amethystRightArmTexture = GameTexture.fromFile("player/armor/amethystarms_right");
        this.sapphireTexture = GameTexture.fromFile("player/armor/sapphirechestplate");
        this.sapphireLeftArmTexture = GameTexture.fromFile("player/armor/sapphirearms_left");
        this.sapphireRightArmTexture = GameTexture.fromFile("player/armor/sapphirearms_right");
        this.emeraldTexture = GameTexture.fromFile("player/armor/emeraldchestplate");
        this.emeraldLeftArmTexture = GameTexture.fromFile("player/armor/emeraldarms_left");
        this.emeraldRightArmTexture = GameTexture.fromFile("player/armor/emeraldarms_right");
        this.rubyTexture = GameTexture.fromFile("player/armor/rubychestplate");
        this.rubyLeftArmTexture = GameTexture.fromFile("player/armor/rubyarms_left");
        this.rubyRightArmTexture = GameTexture.fromFile("player/armor/rubyarms_right");
    }

    @Override
    public GameTexture getArmorLeftArmsTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        if (headItem != null) {
            if (headItem.item.getStringID().equals("amethysthelmet")) {
                return this.amethystLeftArmTexture;
            }
            if (headItem.item.getStringID().equals("sapphireeyepatch")) {
                return this.sapphireLeftArmTexture;
            }
            if (headItem.item.getStringID().equals("emeraldmask")) {
                return this.emeraldLeftArmTexture;
            }
            if (headItem.item.getStringID().equals("rubycrown")) {
                return this.rubyLeftArmTexture;
            }
        }
        return super.getArmorLeftArmsTexture(item, level, player, headItem, chestItem, feetItem);
    }

    @Override
    public GameTexture getArmorRightArmsTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        if (headItem != null) {
            if (headItem.item.getStringID().equals("amethysthelmet")) {
                return this.amethystRightArmTexture;
            }
            if (headItem.item.getStringID().equals("sapphireeyepatch")) {
                return this.sapphireRightArmTexture;
            }
            if (headItem.item.getStringID().equals("emeraldmask")) {
                return this.emeraldRightArmTexture;
            }
            if (headItem.item.getStringID().equals("rubycrown")) {
                return this.rubyRightArmTexture;
            }
        }
        return super.getArmorRightArmsTexture(item, level, player, headItem, chestItem, feetItem);
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

