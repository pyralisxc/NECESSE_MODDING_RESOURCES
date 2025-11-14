/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.util.ArrayList;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class ChickenPeopleSettlementRaidLevelEvent
extends BasicSettlementRaidLevelEvent {
    public ChickenPeopleSettlementRaidLevelEvent() {
        super("chickenraider", "chickenpeopleraidertype");
    }

    @Override
    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
        ArrayList<SettlementRaidLoadout.Weapon> weapons = new ArrayList<SettlementRaidLoadout.Weapon>();
        new SettlementRaidLoadout.Weapon("boxingglovegun").setWeight(0.25f).addTiersToList(weapons, 10, 55);
        new SettlementRaidLoadout.Weapon("snowlauncher").setWeight(0.1f).addTiersToList(weapons, 0, 0);
        generator.weapons(weapons);
        generator.maxWeaponsInPool(4);
    }
}

