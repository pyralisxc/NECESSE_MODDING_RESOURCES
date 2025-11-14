/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.util.ArrayList;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class TheMafiaRaidLevelEvent
extends BasicSettlementRaidLevelEvent {
    public TheMafiaRaidLevelEvent() {
        super("mafiaraider", "themafiaraidertype");
    }

    @Override
    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
        ArrayList<SettlementRaidLoadout.Weapon> weapons = new ArrayList<SettlementRaidLoadout.Weapon>();
        new SettlementRaidLoadout.Weapon("handgun").overrideObtained().setWeight(0.4f).addTiersToList(weapons, 12, 72);
        new SettlementRaidLoadout.Weapon("machinegun").overrideObtained().setWeight(0.25f).addTiersToList(weapons, 8, 36);
        new SettlementRaidLoadout.Weapon("shotgun").overrideObtained().setWeight(0.25f).addTiersToList(weapons, 12, 38);
        new SettlementRaidLoadout.Weapon("sniperrifle").overrideObtained().setWeight(0.1f).addTiersToList(weapons, 1050, Integer.MAX_VALUE, 18, 111);
        generator.weapons(weapons);
        generator.maxWeaponsInPool(8);
    }
}

