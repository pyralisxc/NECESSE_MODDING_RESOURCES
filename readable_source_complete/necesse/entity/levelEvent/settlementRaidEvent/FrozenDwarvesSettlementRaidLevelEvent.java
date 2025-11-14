/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.util.ArrayList;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class FrozenDwarvesSettlementRaidLevelEvent
extends BasicSettlementRaidLevelEvent {
    public FrozenDwarvesSettlementRaidLevelEvent() {
        super("frozendwarfraider", "frozendwarfraidertype");
    }

    @Override
    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
        ArrayList<SettlementRaidLoadout.Weapon> weapons = new ArrayList<SettlementRaidLoadout.Weapon>();
        new SettlementRaidLoadout.Weapon(new InventoryItem("icejavelin", 50)).setWeight(0.2f).addTiersToList(weapons, 100, 1200, 16, 60);
        new SettlementRaidLoadout.Weapon("frostgreatsword").setWeight(0.3f).addTiersToList(weapons, 100, 1350, 38, 98);
        new SettlementRaidLoadout.Weapon("frostspear").setWeight(0.3f).addTiersToList(weapons, 100, 1350, 18, 62);
        new SettlementRaidLoadout.Weapon("froststaff").setWeight(0.15f).addTiersToList(weapons, 100, 1350, 14, 54);
        new SettlementRaidLoadout.Weapon("spiderclaw").setWeight(0.1f).addTiersToList(weapons, 100, Integer.MAX_VALUE, 12, 46);
        new SettlementRaidLoadout.Weapon("sapphirestaff").setWeight(0.1f).addTiersToList(weapons, 100, Integer.MAX_VALUE, 15, 78);
        new SettlementRaidLoadout.Weapon("glacialgreatsword").setWeight(0.25f).addTiersToList(weapons, 1350, Integer.MAX_VALUE, 18, 122);
        new SettlementRaidLoadout.Weapon("glacialbow").setWeight(0.15f).addTiersToList(weapons, 1350, Integer.MAX_VALUE, 15, 78);
        generator.weapons(weapons);
        generator.maxWeaponsInPool(6);
    }
}

