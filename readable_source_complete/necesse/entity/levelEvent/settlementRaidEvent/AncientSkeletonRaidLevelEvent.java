/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.util.ArrayList;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class AncientSkeletonRaidLevelEvent
extends BasicSettlementRaidLevelEvent {
    public AncientSkeletonRaidLevelEvent() {
        super("ancientskeletonraider", "ancientskeletonraidertype");
    }

    @Override
    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
        ArrayList<SettlementRaidLoadout.Weapon> weapons = new ArrayList<SettlementRaidLoadout.Weapon>();
        new SettlementRaidLoadout.Weapon("antiquesword").overrideObtained().setWeight(0.4f).addTiersToList(weapons, 16, 73);
        new SettlementRaidLoadout.Weapon("antiquerifle").overrideObtained().setWeight(0.15f).addTiersToList(weapons, 15, 82);
        new SettlementRaidLoadout.Weapon("antiquebow").setWeight(0.15f).addTiersToList(weapons, 16, 78);
        new SettlementRaidLoadout.Weapon("venomshower").overrideObtained().setWeight(0.1f).addTiersToList(weapons, 12, 46);
        new SettlementRaidLoadout.Weapon("iciclestaff").setWeight(0.1f).addTiersToList(weapons, 16, 72);
        generator.weapons(weapons);
        generator.maxWeaponsInPool(10);
    }
}

