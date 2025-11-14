/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.Settings;
import necesse.engine.journal.JournalEntry;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketJournalChallengeCompleted;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;

public abstract class JournalChallenge
implements IDDataContainer {
    public final IDData idData = new IDData();
    protected GameMessage customName;
    protected JournalEntry attachedJournalEntry;

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    @Override
    public String getStringID() {
        return this.idData.getStringID();
    }

    @Override
    public int getID() {
        return this.idData.getID();
    }

    public void onChallengeRegistryClosed() {
    }

    public JournalChallenge setCustomName(GameMessage name) {
        if (JournalChallengeRegistry.instance.isClosed()) {
            throw new IllegalStateException("Cannot change journal challenge name after registry has closed");
        }
        this.customName = name;
        return this;
    }

    public GameMessage getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new LocalMessage("journal", this.getStringID());
    }

    public void setAttachedJournal(JournalEntry journalEntry) {
        if (this.attachedJournalEntry != null) {
            throw new IllegalStateException("Cannot attach challenge " + this.getStringID() + " to multiple journals");
        }
        this.attachedJournalEntry = journalEntry;
    }

    public boolean isJournalEntryDiscovered(ServerClient client) {
        return this.attachedJournalEntry != null && this.attachedJournalEntry.isDiscovered(client);
    }

    public boolean isJournalEntryDiscovered(Client client) {
        return this.attachedJournalEntry != null && this.attachedJournalEntry.isDiscovered(client);
    }

    protected boolean isCompleted(PlayerStats stats) {
        return stats.challenges_completed.isChallengeCompleted(this.getStringID());
    }

    public final boolean isCompleted(Client client) {
        return this.isCompleted(client.characterStats);
    }

    public final boolean isCompleted(ServerClient serverClient) {
        return this.isCompleted(serverClient.characterStats());
    }

    public void markCompleted(ServerClient serverClient) {
        boolean hasCompleted = serverClient.characterStats().challenges_completed.isChallengeCompleted(this.getStringID());
        serverClient.newStats.challenges_completed.completeChallenge(this.getStringID());
        serverClient.forceCombineNewStats();
        GameMessage name = this.getName();
        if (name != null && this.attachedJournalEntry != null && serverClient.hasSubmittedCharacter() && !hasCompleted) {
            boolean rewardAvailable = this.attachedJournalEntry.checkRewardAvailable(serverClient);
            serverClient.sendPacket(new PacketJournalChallengeCompleted(this.attachedJournalEntry.getID(), this.getID(), rewardAvailable));
        }
    }

    protected boolean isClaimed(PlayerStats stats) {
        return stats.challenges_claimed.isChallengeClaimed(this.getStringID());
    }

    public final boolean isClaimed(Client client) {
        return this.isClaimed(client.characterStats);
    }

    public final boolean isClaimed(ServerClient serverClient) {
        return this.isClaimed(serverClient.characterStats());
    }

    public void markClaimed(ServerClient serverClient) {
        serverClient.newStats.challenges_claimed.markChallengeClaimed(this.getStringID());
    }

    protected void addChallengeLabelInJournal(Client client, FormContentBox contentBox, FormFlow flow, int maxWidth, boolean challengeComplete) {
        FormFairTypeLabel label = new FormFairTypeLabel(this.getName(), 10, 0);
        label.setMaxWidth(maxWidth);
        if (challengeComplete) {
            label.setColor(Settings.UI.successTextColor);
        }
        label.setParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(16, true, FairItemGlyph::onlyShowNameTooltip), TypeParsers.MobIcon(16));
        contentBox.addComponent(flow.nextY(label, 4));
    }

    public abstract void addJournalFormContent(Client var1, FormContentBox var2, FormFlow var3);

    public boolean hasReward(NetworkClient client) {
        return !this.getReward().items.isEmpty();
    }

    public LootTable getReward() {
        return new LootTable();
    }

    public void addRewardsToList(LootList list, Client client) {
        this.getReward().addPossibleLoot(list, new Object[0]);
    }
}

