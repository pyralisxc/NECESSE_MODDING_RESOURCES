/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.agedChampion;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class AgedChampionHelmetArmorItem
extends SetHelmetArmorItem {
    public FloatUpgradeValue resDecay = new FloatUpgradeValue().setBaseValue(-0.5f).setUpgradedValue(1.0f, -1.0f);

    public AgedChampionHelmetArmorItem() {
        super(28, DamageTypeRegistry.MELEE, 1600, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.EPIC, "agedchampionhelmet", "agedchampionchestplate", "agedchampiongreaves", "agedchampionsetbonus");
        this.canBeUsedForRaids = true;
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.MELEE_CRIT_CHANCE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.RESILIENCE_DECAY, this.resDecay.getValue(this.getUpgradeTier(item))));
    }
}

