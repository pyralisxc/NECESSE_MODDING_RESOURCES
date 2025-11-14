/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.MobsKilledJournalChallenge;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public class DefeatPiratesOnBoatJournalChallenge
extends MobsKilledJournalChallenge {
    public static String[] BOAT_MOB_STRING_IDS = new String[]{"woodboatmount", "steelboat", "woodboat"};

    public DefeatPiratesOnBoatJournalChallenge() {
        super(5, "piratecaptain", "piraterecruit", "pirateparrot");
    }

    @Override
    public void onMobKilled(ServerClient serverClient, Mob mob) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        if (!serverClient.playerMob.isRiding()) {
            return;
        }
        boolean validMob = false;
        for (String mobStringID : this.mobStringIDs) {
            if (!mob.getStringID().equals(mobStringID)) continue;
            validMob = true;
            break;
        }
        if (!validMob) {
            return;
        }
        Mob mount = serverClient.playerMob.getMount();
        if (mount == null) {
            return;
        }
        for (String boatStringID : BOAT_MOB_STRING_IDS) {
            if (!mount.getStringID().equals(boatStringID)) continue;
            this.addKill(serverClient);
            return;
        }
    }
}

