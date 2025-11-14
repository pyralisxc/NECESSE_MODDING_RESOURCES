/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class GenericSettler
extends Settler {
    public GenericSettler() {
        super("human");
    }

    @Override
    public void loadTextures() {
        this.texture = GameTexture.fromFile("mobs/icons/human");
    }

    @Override
    public GameMessage getAcquireTip() {
        return new LocalMessage("settlement", "foundinvillagetip");
    }

    @Override
    public void addNewRecruitSettler(ServerSettlementData data, boolean isRandomEvent, TicketSystemList<Supplier<HumanMob>> ticketSystem) {
        ticketSystem.addObject(isRandomEvent ? 100 : 50, (Object)this.getNewRecruitMob(data));
    }
}

