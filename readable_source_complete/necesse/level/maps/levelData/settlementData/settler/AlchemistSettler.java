/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class AlchemistSettler
extends Settler {
    public AlchemistSettler() {
        super("alchemisthuman");
    }

    @Override
    public GameMessage getAcquireTip() {
        return new LocalMessage("settlement", "foundinvillagetip");
    }

    @Override
    public void addNewRecruitSettler(ServerSettlementData data, boolean isRandomEvent, TicketSystemList<Supplier<HumanMob>> ticketSystem) {
        if ((isRandomEvent || !this.doesSettlementHaveThisSettler(data)) && data.hasCompletedQuestTier("evilsprotector")) {
            ticketSystem.addObject(75, (Object)this.getNewRecruitMob(data));
        }
    }
}

