/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.recipe.Recipe;

public interface CraftedRecipeJournalChallengeListener {
    public void onCraftedRecipe(ServerClient var1, Recipe var2, int var3);
}

