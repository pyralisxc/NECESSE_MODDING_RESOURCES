/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.battlechef;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.lootTable.presets.IncursionArmorSetsLootTable;
import necesse.inventory.lootTable.presets.IncursionHeadArmorLootTable;

public class BattleChefHatArmorItem
extends SetHelmetArmorItem {
    public IntUpgradeValue maxHealth = new IntUpgradeValue().setBaseValue(10).setUpgradedValue(5.0f, 20);

    public BattleChefHatArmorItem() {
        super(19, DamageTypeRegistry.MELEE, 2000, IncursionHeadArmorLootTable.incursionHeadArmor, IncursionArmorSetsLootTable.incursionArmorSets, Item.Rarity.EPIC, "battlechefhat", "battlechefchestplate", "battlechefboots", "battlechefsetbonus");
        this.canBeUsedForRaids = true;
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.STAMINA_REGEN, Float.valueOf(-0.25f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, this.maxHealth.getValue(this.getUpgradeTier(item))));
    }
}

