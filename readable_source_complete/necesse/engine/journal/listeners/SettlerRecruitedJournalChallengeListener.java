/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public interface SettlerRecruitedJournalChallengeListener {
    public void onSettlerRecruited(ServerClient var1, ServerClient var2, Level var3, ServerSettlementData var4, LevelSettler var5, HumanMob var6);
}

