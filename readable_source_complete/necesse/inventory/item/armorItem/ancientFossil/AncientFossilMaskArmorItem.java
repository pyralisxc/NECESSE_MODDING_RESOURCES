/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.ancientFossil;

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

public class AncientFossilMaskArmorItem
extends SetHelmetArmorItem {
    public AncientFossilMaskArmorItem() {
        super(8, DamageTypeRegistry.SUMMON, 1750, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "ancientfossilmask", "ancientfossilchestplate", "ancientfossilboots", "ancientfossilmasksetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Integer>(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue<Float>(BuffModifiers.SUMMON_DAMAGE, Float.valueOf(0.15f)));
    }
}

