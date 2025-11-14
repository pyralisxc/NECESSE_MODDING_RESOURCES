/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import java.util.ArrayList;
import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.BuffGainedJournalChallengeListener;
import necesse.engine.journal.listeners.LevelChangedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.level.maps.Level;

public class BecomePossessedByAForestSpectorJournalChallenge
extends SimpleJournalChallenge
implements BuffGainedJournalChallengeListener,
LevelChangedJournalChallengeListener {
    @Override
    public void onChallengeRegistryClosed() {
        super.onChallengeRegistryClosed();
    }

    @Override
    public void onBuffGained(ServerClient serverClient, PlayerMob player, ActiveBuff activeBuff, boolean isOverride) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        Level level = player.getLevel();
        if (!level.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return;
        }
        if (!JournalChallengeUtils.isPlainsBiome(level.getBiome(player.getTileX(), player.getTileY()))) {
            return;
        }
        this.checkBuffs(serverClient, player.buffManager);
    }

    @Override
    public void onLevelChanged(ServerClient serverClient, Level oldLevel, Level newLevel) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        if (!newLevel.getIdentifier().equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return;
        }
        if (!JournalChallengeUtils.isPlainsBiome(newLevel.getBiome(serverClient.playerMob.getTileX(), serverClient.playerMob.getTileY()))) {
            return;
        }
        this.checkBuffs(serverClient, serverClient.playerMob.buffManager);
    }

    protected void checkBuffs(ServerClient serverClient, BuffManager buffManager) {
        ArrayList<ActiveBuff> buffs = buffManager.getArrayBuffs();
        for (ActiveBuff buff : buffs) {
            if (buff.buff != BuffRegistry.Debuffs.SPIRIT_POSSESSED) continue;
            this.markCompleted(serverClient);
            serverClient.forceCombineNewStats();
            return;
        }
    }
}

