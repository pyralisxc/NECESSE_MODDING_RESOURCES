/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.SettlerRecruitedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class FreeStylistJournalChallenge
extends SimpleJournalChallenge
implements SettlerRecruitedJournalChallengeListener {
    @Override
    public void onSettlerRecruited(ServerClient serverClient, ServerClient recruiterClient, Level recruitedOnLevel, ServerSettlementData settlement, LevelSettler settler, HumanMob humanMob) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        if (humanMob == null || !humanMob.getStringID().equals("stylisthuman")) {
            return;
        }
        if (!(humanMob instanceof StylistHumanMob) || !((StylistHumanMob)humanMob).wasTrappedByPirates) {
            return;
        }
        this.markCompleted(serverClient);
        serverClient.forceCombineNewStats();
    }
}

