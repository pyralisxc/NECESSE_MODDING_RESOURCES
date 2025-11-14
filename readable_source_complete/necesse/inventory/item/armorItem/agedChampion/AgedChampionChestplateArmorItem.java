/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.agedChampion;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class AgedChampionChestplateArmorItem
extends ChestArmorItem {
    public AgedChampionChestplateArmorItem() {
        super(28, 1600, Item.Rarity.EPIC, "agedchampionchestplate", "agedchampionarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.MELEE_CRIT_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.STAMINA_REGEN, Float.valueOf(0.1f)));
    }
}

