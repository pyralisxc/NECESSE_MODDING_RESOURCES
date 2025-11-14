/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.ancientFossil;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class AncientFossilHelmetArmorItem
extends SetHelmetArmorItem {
    public AncientFossilHelmetArmorItem() {
        super(28, null, 1750, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "ancientfossilhelmet", "ancientfossilchestplate", "ancientfossilboots", "ancientfossilhelmetsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.MELEE_ATTACK_SPEED, Float.valueOf(0.15f)));
    }
}

