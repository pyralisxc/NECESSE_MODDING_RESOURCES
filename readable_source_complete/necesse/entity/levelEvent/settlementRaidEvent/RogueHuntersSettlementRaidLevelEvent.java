/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.util.ArrayList;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class RogueHuntersSettlementRaidLevelEvent
extends BasicSettlementRaidLevelEvent {
    public RogueHuntersSettlementRaidLevelEvent() {
        super("roguehunterraider", "roguehuntersraidertype");
    }

    @Override
    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
        ArrayList<SettlementRaidLoadout.Weapon> weapons = new ArrayList<SettlementRaidLoadout.Weapon>();
        weapons.add(new SettlementRaidLoadout.Weapon("woodbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("copperbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("ironbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("goldbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("frostbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("demonicbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("ivybow"));
        weapons.add(new SettlementRaidLoadout.Weapon("vulturesburst"));
        weapons.add(new SettlementRaidLoadout.Weapon("tungstenbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("glacialbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("bowofdualism"));
        weapons.add(new SettlementRaidLoadout.Weapon("antiquebow"));
        weapons.add(new SettlementRaidLoadout.Weapon("thecrimsonsky"));
        weapons.add(new SettlementRaidLoadout.Weapon("arachnidwebbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("goldgreatbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("voidgreatbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("ivygreatbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("tungstengreatbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("myceliumgreatbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("druidsgreatbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("slimegreatbow"));
        weapons.add(new SettlementRaidLoadout.Weapon("nightpiercer"));
        weapons.add(new SettlementRaidLoadout.Weapon("theravensnest"));
        generator.weapons(weapons);
        generator.maxWeaponsInPool(5);
    }
}

