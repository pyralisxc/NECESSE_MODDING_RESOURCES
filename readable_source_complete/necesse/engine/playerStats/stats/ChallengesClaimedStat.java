/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class ChallengesClaimedStat
extends GameStat {
    protected HashSet<String> dirtyChallenges = new HashSet();
    protected HashSet<String> claimedChallenges = new HashSet();

    public ChallengesClaimedStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyChallenges.clear();
    }

    protected void markChallengeClaimed(String challengeStringID, boolean updatePlatform) {
        if (this.claimedChallenges.contains(challengeStringID)) {
            return;
        }
        if (JournalChallengeRegistry.doesChallengeExists(challengeStringID)) {
            this.claimedChallenges.add(challengeStringID);
            if (updatePlatform) {
                this.updatePlatform();
            }
            this.markImportantDirty();
            this.dirtyChallenges.add(challengeStringID);
        }
    }

    public void markChallengeClaimed(String challengeStringID) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.markChallengeClaimed(challengeStringID, true);
    }

    public boolean isChallengeClaimed(String challengeStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.claimedChallenges.contains(challengeStringID);
    }

    public boolean isChallengeClaimed(int challengeID) {
        return this.isChallengeClaimed(JournalChallengeRegistry.getChallengeStringID(challengeID));
    }

    public Iterable<String> getChallengesClaimed() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.claimedChallenges;
    }

    public int getTotalClaimedChallenges() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.claimedChallenges.size();
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof ChallengesClaimedStat) {
            ChallengesClaimedStat other = (ChallengesClaimedStat)stat;
            for (String challengeStringID : other.claimedChallenges) {
                this.markChallengeClaimed(challengeStringID, true);
            }
        }
    }

    @Override
    public void resetCombine() {
        this.dirtyChallenges.clear();
        this.claimedChallenges.clear();
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            // empty if block
        }
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
    }

    @Override
    public void addSaveData(SaveData save) {
        if (this.claimedChallenges.isEmpty()) {
            return;
        }
        save.addStringHashSet("claimedchallenges", this.claimedChallenges);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.claimedChallenges.clear();
        for (String challengeStringID : save.getStringHashSet("claimedchallenges", new HashSet<String>())) {
            if (challengeStringID.isEmpty()) continue;
            this.markChallengeClaimed(challengeStringID, false);
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.claimedChallenges.size());
        for (String challengeStringID : this.claimedChallenges) {
            writer.putNextShortUnsigned(JournalChallengeRegistry.getChallengeID(challengeStringID));
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.claimedChallenges.clear();
        this.dirtyChallenges.clear();
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int challengeID = reader.getNextShortUnsigned();
            String challengeStringID = JournalChallengeRegistry.getChallengeStringID(challengeID);
            this.markChallengeClaimed(challengeStringID, true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyChallenges.size());
        for (String challengeStringID : this.dirtyChallenges) {
            writer.putNextShortUnsigned(JournalChallengeRegistry.getChallengeID(challengeStringID));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int challengeID = reader.getNextShortUnsigned();
            String challengeStringID = JournalChallengeRegistry.getChallengeStringID(challengeID);
            this.markChallengeClaimed(challengeStringID, true);
        }
    }
}

