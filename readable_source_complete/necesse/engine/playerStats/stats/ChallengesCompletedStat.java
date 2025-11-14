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

public class ChallengesCompletedStat
extends GameStat {
    protected HashSet<String> dirtyChallenges = new HashSet();
    protected HashSet<String> completedChallenges = new HashSet();

    public ChallengesCompletedStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyChallenges.clear();
    }

    protected void completeChallenge(String challengeStringID, boolean updatePlatform) {
        if (this.completedChallenges.contains(challengeStringID)) {
            return;
        }
        if (JournalChallengeRegistry.doesChallengeExists(challengeStringID)) {
            this.completedChallenges.add(challengeStringID);
            if (updatePlatform) {
                this.updatePlatform();
            }
            this.markImportantDirty();
            this.dirtyChallenges.add(challengeStringID);
        }
    }

    public void completeChallenge(String challengeStringID) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.completeChallenge(challengeStringID, true);
    }

    public boolean isChallengeCompleted(String challengeStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.completedChallenges.contains(challengeStringID);
    }

    public boolean isChallengeCompleted(int challengeID) {
        return this.isChallengeCompleted(JournalChallengeRegistry.getChallengeStringID(challengeID));
    }

    public Iterable<String> getChallengesCompleted() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.completedChallenges;
    }

    public int getTotalCompletedChallenges() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.completedChallenges.size();
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof ChallengesCompletedStat) {
            ChallengesCompletedStat other = (ChallengesCompletedStat)stat;
            for (String challengeStringID : other.completedChallenges) {
                this.completeChallenge(challengeStringID, true);
            }
        }
    }

    @Override
    public void resetCombine() {
        this.dirtyChallenges.clear();
        this.completedChallenges.clear();
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
        if (this.completedChallenges.isEmpty()) {
            return;
        }
        save.addStringHashSet("completedchallenges", this.completedChallenges);
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.completedChallenges.clear();
        for (String challengeStringID : save.getStringHashSet("completedchallenges", new HashSet<String>())) {
            if (challengeStringID.isEmpty()) continue;
            this.completeChallenge(challengeStringID, false);
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.completedChallenges.size());
        for (String challengeStringID : this.completedChallenges) {
            writer.putNextShortUnsigned(JournalChallengeRegistry.getChallengeID(challengeStringID));
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.completedChallenges.clear();
        this.dirtyChallenges.clear();
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int challengeID = reader.getNextShortUnsigned();
            String challengeStringID = JournalChallengeRegistry.getChallengeStringID(challengeID);
            this.completeChallenge(challengeStringID, true);
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
            this.completeChallenge(challengeStringID, true);
        }
    }
}

