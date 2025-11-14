/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.IntJournalChallenge;
import necesse.engine.journal.listeners.CraftedRecipeJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.inventory.recipe.Recipe;

public class CraftItemsJournalChallenge
extends IntJournalChallenge
implements CraftedRecipeJournalChallengeListener {
    protected String[] itemStringIDs;
    protected boolean countResultAmount;

    public CraftItemsJournalChallenge(int total, boolean countResultAmount, String ... itemStringIDs) {
        super(total);
        this.itemStringIDs = itemStringIDs;
        this.countResultAmount = countResultAmount;
    }

    @Override
    public void markCompleted(ServerClient serverClient) {
        super.markCompleted(serverClient);
        serverClient.newStats.challenges_data.clearKey(this.getStringID() + "Crafts");
    }

    @Override
    protected int getProgress(PlayerStats stats) {
        return stats.challenges_data.getData().getInt(this.getStringID() + "Crafts");
    }

    @Override
    public void onCraftedRecipe(ServerClient serverClient, Recipe recipe, int amount) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        for (String itemStringID : this.itemStringIDs) {
            if (!recipe.resultStringID.equals(itemStringID)) continue;
            int next = this.getProgress(serverClient.characterStats()) + (this.countResultAmount ? recipe.resultAmount : 1) * amount;
            serverClient.newStats.challenges_data.getData().setInt(this.getStringID() + "Crafts", next);
            if (next >= this.max) {
                this.markCompleted(serverClient);
            }
            serverClient.forceCombineNewStats();
            return;
        }
    }
}

