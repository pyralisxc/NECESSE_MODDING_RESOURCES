/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class CrateLootTable
extends LootTable {
    public static final OneOfLootItems basicBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 1));
    public static final OneOfLootItems snowBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 2), LootItem.between("frostshard", 1, 2));
    public static final OneOfLootItems swampBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 2), LootItem.between("ivybar", 1, 2));
    public static final OneOfLootItems desertBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 2), LootItem.between("quartz", 1, 2));
    public static final OneOfLootItems plainsBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 2), LootItem.between("runestone", 1, 2));
    public static final OneOfLootItems potions = new OneOfLootItems(new LootItem("attackspeedpotion"), new LootItem("healthregenpotion"), new LootItem("manaregenpotion"), new LootItem("fishingpotion"), new LootItem("fireresistancepotion"), new LootItem("resistancepotion"), new LootItem("speedpotion"), new LootItem("battlepotion"), new LootItem("thornspotion"), new LootItem("accuracypotion"), new LootItem("knockbackpotion"), new LootItem("rapidpotion"));
    public static final CrateLootTable basicCrate = new CrateLootTable((LootItemInterface)basicBars);
    public static final CrateLootTable snowCrate = new CrateLootTable((LootItemInterface)snowBars);
    public static final CrateLootTable swampCrate = new CrateLootTable((LootItemInterface)swampBars);
    public static final CrateLootTable desertCrate = new CrateLootTable((LootItemInterface)desertBars);
    public static final CrateLootTable plainsCrate = new CrateLootTable((LootItemInterface)plainsBars);
    public OneOfTicketLootItems oneOfItems;

    private CrateLootTable(LootItemInterface bars) {
        super(LootItem.offset("coin", 12, 5).splitItems(5));
        this.oneOfItems = new OneOfTicketLootItems(100, LootItem.offset("stonearrow", 10, 5), 100, LootItem.offset("firearrow", 10, 5), 100, LootItem.offset("ironarrow", 10, 5), 75, LootItem.between("healthpotion", 1, 2), 50, LootItem.between("manapotion", 1, 2), 100, LootItem.offset("ninjastar", 8, 3), 100, LootItem.offset("torch", 7, 3), 25, new LootItem("recallscroll"), 50, bars, 50, potions);
        this.items.add(this.oneOfItems);
    }
}

