/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.mycelium;

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

public class MyceliumScarfArmorItem
extends SetHelmetArmorItem {
    public MyceliumScarfArmorItem() {
        super(6, DamageTypeRegistry.SUMMON, 1600, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "myceliumscarf", "myceliumchestplate", "myceliumboots", "myceliumscarfsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.UNDER_HAIR;
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Integer>(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue<Float>(BuffModifiers.SUMMONS_SPEED, Float.valueOf(0.1f)));
    }
}

