/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import necesse.engine.quest.KillMobsSettlementQuest;
import necesse.engine.quest.Quest;
import necesse.entity.mobs.Mob;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SimpleSettlementQuestTier;

public abstract class SimpleBossSettlementQuestTier
extends SimpleSettlementQuestTier {
    public String bossStringID;

    public SimpleBossSettlementQuestTier(String stringID, String bossStringID) {
        super(stringID);
        this.bossStringID = bossStringID;
    }

    @Override
    public Quest getTierCompleteQuest(ServerSettlementData data, Mob fromBossKillMob) {
        KillMobsSettlementQuest killQuest = new KillMobsSettlementQuest(data.getSettlementName(), this.stringID, this.bossStringID, 1);
        if (fromBossKillMob != null) {
            killQuest.addExtraKills(fromBossKillMob.getID(), 1);
        }
        return killQuest;
    }

    @Override
    public boolean bossKillTriggersTierQuest(Mob mob) {
        return mob.getStringID().equals(this.bossStringID);
    }

    @Override
    public int getTotalTierQuests() {
        return 0;
    }

    @Override
    public int getTotalBasicQuests() {
        return 0;
    }
}

