/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.lootTable.LootTable;

public class AdventureJournalContainer
extends Container {
    public final IntCustomAction claimChallengeRewardButton;

    public AdventureJournalContainer(final NetworkClient client, int uniqueSeed) {
        super(client, uniqueSeed);
        this.claimChallengeRewardButton = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                JournalChallenge challenge = JournalChallengeRegistry.getChallenge(value);
                if (client.isServer()) {
                    ServerClient serverClient = client.getServerClient();
                    if (challenge.isClaimed(serverClient)) {
                        GameLog.warn.println(serverClient.getName() + " tried to claim challenge reward that is already claimed");
                    } else if (!challenge.isCompleted(serverClient)) {
                        GameLog.warn.println(serverClient.getName() + " tried to claim challenge reward that is not completed");
                    } else {
                        challenge.markClaimed(serverClient);
                        serverClient.forceCombineNewStats();
                        LootTable reward = challenge.getReward();
                        ArrayList<InventoryItem> rewards = reward.getNewList(GameRandom.globalRandom, 1.0f, serverClient);
                        for (InventoryItem item : rewards) {
                            serverClient.playerMob.getInv().addItemsDropRemaining(item, "reward", client.playerMob, true, true, true);
                        }
                        AdventureJournalContainer.this.close();
                    }
                }
            }
        });
    }
}

