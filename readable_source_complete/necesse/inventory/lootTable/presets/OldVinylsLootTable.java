/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class OldVinylsLootTable
extends LootTable {
    public static OneOfLootItems oldVinyls = new OneOfLootItems(new LootItem("homevinyl"), new LootItem("waterfaevinyl"), new LootItem("homeatlastvinyl"), new LootItem("musesvinyl"), new LootItem("runningvinyl"), new LootItem("grindthealarmsvinyl"), new LootItem("eyesofthedesertvinyl"), new LootItem("elektrakvinyl"), new LootItem("thecontrolroomvinyl"), new LootItem("runningvinyl"), new LootItem("telltalevinyl"), new LootItem("silverlakevinyl"), new LootItem("bythefieldvinyl"), new LootItem("sunstonesvinyl"), new LootItem("caravantusksvinyl"), new LootItem("kronosvinyl"), new LootItem("lostgripvinyl"), new LootItem("airlockfailurevinyl"), new LootItem("konsoleglitchvinyl"), new LootItem("rialtovinyl"), new LootItem("icyrusevinyl"), new LootItem("icestarvinyl"), new LootItem("awayvinyl"), new LootItem("beatdownvinyl"), new LootItem("siegevinyl"), new LootItem("halodromevinyl"), new LootItem("milleniumvinyl"), new LootItem("kandiruvinyl"));
    public static final OldVinylsLootTable instance = new OldVinylsLootTable();

    private OldVinylsLootTable() {
        super(oldVinyls);
    }
}

