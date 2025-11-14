/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.util.ArrayList;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class VampiresSettlementRaidLevelEvent
extends BasicSettlementRaidLevelEvent {
    public VampiresSettlementRaidLevelEvent() {
        super("vampireraider", "vampireraidertype");
    }

    @Override
    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
        ArrayList<SettlementRaidLoadout.Weapon> weapons = new ArrayList<SettlementRaidLoadout.Weapon>();
        new SettlementRaidLoadout.Weapon("venomstaff").setWeight(0.1f).addTiersToList(weapons, 100, 2000, 12, 38);
        new SettlementRaidLoadout.Weapon("demonicsword").setWeight(0.25f).addTiersToList(weapons, 100, 2000, 16, 74);
        new SettlementRaidLoadout.Weapon("demonicbow").setWeight(0.1f).addTiersToList(weapons, 100, 2000, 14, 88);
        new SettlementRaidLoadout.Weapon("bloodvolley").setWeight(0.15f).addTiersToList(weapons, 100, Integer.MAX_VALUE, 12, 56);
        new SettlementRaidLoadout.Weapon("thecrimsonsky").setWeight(0.1f).overrideObtained().addTiersToList(weapons, 2000, Integer.MAX_VALUE, 14, 62);
        new SettlementRaidLoadout.Weapon("nightpiercer").setWeight(0.1f).overrideObtained().addTiersToList(weapons, 2000, Integer.MAX_VALUE, 16, 111);
        new SettlementRaidLoadout.Weapon("bloodclaw").setWeight(0.25f).overrideObtained().addTiersToList(weapons, 2000, Integer.MAX_VALUE, 12, 42);
        generator.weapons(weapons);
        generator.maxWeaponsInPool(5);
    }
}

