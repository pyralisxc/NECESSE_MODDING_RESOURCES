/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.inventory.lootTable.presets.CrateLootTable;

public class WitchCrateLootTable
extends LootTable {
    public static final RotationLootItem witchArmorSet = RotationLootItem.presetRotation(new LootItem("witchhat"), new LootItem("witchrobe"), new LootItem("witchshoes"));
    public static final RotationLootItem weaponsAndTrinkets = RotationLootItem.presetRotation(new LootItem("necroticflask"), new LootItem("necroticgreatsword"), new LootItem("necroticbow"), new LootItem("necroticsoulskull"));
    public static final LootTable evilWitchChest = new LootTable(weaponsAndTrinkets);
    public static final OneOfLootItems potions = new OneOfLootItems(new LootItem("attackspeedpotion"), new LootItem("healthregenpotion"), new LootItem("manaregenpotion"), new LootItem("fishingpotion"), new LootItem("fireresistancepotion"), new LootItem("resistancepotion"), new LootItem("speedpotion"), new LootItem("battlepotion"), new LootItem("thornspotion"), new LootItem("accuracypotion"), new LootItem("knockbackpotion"), new LootItem("rapidpotion"));
    public static final WitchCrateLootTable instance = new WitchCrateLootTable();

    private WitchCrateLootTable() {
        super(new ChanceLootItem(0.5f, "enchantingscroll"), LootItem.offset("coin", 35, 5), CrateLootTable.basicCrate, CrateLootTable.basicCrate, potions, potions, potions, LootItem.between("batwing", 3, 7));
    }
}

