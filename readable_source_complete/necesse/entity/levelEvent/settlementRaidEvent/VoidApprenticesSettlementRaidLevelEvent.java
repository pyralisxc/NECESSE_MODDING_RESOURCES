/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.util.ArrayList;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class VoidApprenticesSettlementRaidLevelEvent
extends BasicSettlementRaidLevelEvent {
    public VoidApprenticesSettlementRaidLevelEvent() {
        super("voidapprenticeraider", "voidapprenticeraidertype");
    }

    @Override
    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
        ArrayList<SettlementRaidLoadout.Weapon> weapons = new ArrayList<SettlementRaidLoadout.Weapon>();
        new SettlementRaidLoadout.Weapon("bloodbolt").setWeight(0.25f).addTiersToList(weapons, 16, 86);
        new SettlementRaidLoadout.Weapon("voidmissile").setWeight(0.15f).addTiersToList(weapons, 13, 88);
        new SettlementRaidLoadout.Weapon("lightninghammer").overrideObtained().setWeight(0.25f).addTiersToList(weapons, 14, 54);
        new SettlementRaidLoadout.Weapon("voidstaff").overrideObtained().setWeight(0.15f).addTiersToList(weapons, 13, 72);
        generator.weapons(weapons);
        generator.maxWeaponsInPool(8);
    }
}

