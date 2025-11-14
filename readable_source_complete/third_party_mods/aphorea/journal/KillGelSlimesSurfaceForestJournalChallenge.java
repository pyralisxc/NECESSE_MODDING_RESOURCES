/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.journal.JournalChallengeUtils
 *  necesse.engine.journal.MobsKilledJournalChallenge
 *  necesse.engine.network.server.ServerClient
 *  necesse.entity.mobs.Mob
 *  necesse.level.maps.Level
 *  necesse.level.maps.biomes.Biome
 */
package aphorea.journal;

import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.journal.MobsKilledJournalChallenge;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;

public class KillGelSlimesSurfaceForestJournalChallenge
extends MobsKilledJournalChallenge {
    public KillGelSlimesSurfaceForestJournalChallenge() {
        super(25, new String[]{"gelslime"});
    }

    public void onMobKilled(ServerClient serverClient, Mob mob) {
        Level level = mob.getLevel();
        if (!level.isCave && JournalChallengeUtils.isForestBiome((Biome)level.getBiome(mob.getTileX(), mob.getTileY()))) {
            super.onMobKilled(serverClient, mob);
        }
    }
}

