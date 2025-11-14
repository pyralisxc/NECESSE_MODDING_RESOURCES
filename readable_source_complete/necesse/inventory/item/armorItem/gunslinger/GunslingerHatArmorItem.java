/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.gunslinger;

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

public class GunslingerHatArmorItem
extends SetHelmetArmorItem {
    public GunslingerHatArmorItem() {
        super(13, DamageTypeRegistry.RANGED, 1000, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "gunslingerhat", "gunslingervest", "gunslingerboots", "gunslingersetbonus");
        this.canBeUsedForRaids = true;
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.hairMaskTextureName = "animalskeeperhat_hairmask";
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.RANGED_CRIT_CHANCE, Float.valueOf(0.1f)));
    }
}

