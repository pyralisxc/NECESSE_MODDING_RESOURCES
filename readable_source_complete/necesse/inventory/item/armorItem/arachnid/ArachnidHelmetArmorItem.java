/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.arachnid;

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

public class ArachnidHelmetArmorItem
extends SetHelmetArmorItem {
    public ArachnidHelmetArmorItem() {
        super(1, DamageTypeRegistry.MELEE, 550, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "arachnidhelmet", "arachnidchestplate", "arachnidlegs", "arachnidsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
        this.canBeUsedForRaids = true;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.SUMMON_ATTACK_SPEED, Float.valueOf(0.05f)), new ModifierValue<Integer>(BuffModifiers.MAX_SUMMONS, 1));
    }
}

