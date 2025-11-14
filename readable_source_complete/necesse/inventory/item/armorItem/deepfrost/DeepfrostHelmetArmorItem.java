/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.deepfrost;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class DeepfrostHelmetArmorItem
extends SetHelmetArmorItem {
    public DeepfrostHelmetArmorItem() {
        super(18, DamageTypeRegistry.MELEE, 1450, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "deepfrosthood", "deepfrostchestplate", "deepfrostboots", "deepfrostsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.CRIT_DAMAGE, Float.valueOf(0.1f)));
    }
}

