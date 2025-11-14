/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.quest;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.quest.KillMobsQuest;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootList;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public class KillMobsSettlementQuest
extends KillMobsQuest {
    public GameMessage settlementName;
    public String questTier;

    public KillMobsSettlementQuest() {
    }

    public KillMobsSettlementQuest(GameMessage settlementName, String questTier, KillMobsQuest.KillObjective firstObjective, KillMobsQuest.KillObjective ... extraObjectives) {
        super(firstObjective, extraObjectives);
        this.settlementName = settlementName;
        this.questTier = questTier;
    }

    public KillMobsSettlementQuest(GameMessage settlementName, String questTier, String mobStringID, int mobsToKill) {
        super(mobStringID, mobsToKill);
        this.settlementName = settlementName;
        this.questTier = questTier;
    }

    public KillMobsSettlementQuest(GameMessage settlementName, String questTier, int mobID, int mobsToKill) {
        super(mobID, mobsToKill);
        this.settlementName = settlementName;
        this.questTier = questTier;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.settlementName != null) {
            save.addSaveData(this.settlementName.getSaveData("settlementName"));
        }
        if (this.questTier != null) {
            save.addSafeString("questTier", this.questTier);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.settlementName = GameMessage.loadSave(save, "settlementName", false);
        this.questTier = save.getSafeString("questTier", null, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        if (this.settlementName != null) {
            writer.putNextBoolean(true);
            this.settlementName.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
        int tierQuestIndex = this.questTier == null ? -1 : SettlementQuestTier.getTierIndex(this.questTier);
        writer.putNextShort((short)tierQuestIndex);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        SettlementQuestTier tier;
        short questTierIndex;
        super.applySpawnPacket(reader);
        if (reader.getNextBoolean()) {
            this.settlementName = GameMessage.fromPacket(reader);
        }
        if ((questTierIndex = reader.getNextShort()) != -1 && (tier = SettlementQuestTier.getTier(questTierIndex)) != null) {
            this.questTier = tier.stringID;
        }
    }

    @Override
    public GameMessage getTitle() {
        return new LocalMessage("quests", "settlementquest", "settlement", this.settlementName == null ? new LocalMessage("ui", "settlement") : this.settlementName);
    }

    @Override
    public FairType getRewardType(NetworkClient client, boolean outlined) {
        if (this.questTier == null) {
            return null;
        }
        SettlementQuestTier questTier = SettlementQuestTier.getTier(this.questTier);
        if (questTier == null) {
            return null;
        }
        LootList rewards = questTier.getTierRewardsDisplayList();
        StringBuilder builder = new StringBuilder();
        boolean isFirstReward = true;
        for (Item item : rewards.getItems()) {
            if (!isFirstReward) {
                builder.append(", ");
            }
            builder.append(TypeParsers.getItemParseString(new InventoryItem(item)));
            builder.append(" ").append(ItemRegistry.getDisplayName(item.getID()));
            isFirstReward = false;
        }
        if (builder.length() == 0) {
            return null;
        }
        FontOptions rewardFontOptions = new FontOptions(12).outline(outlined);
        FairType fairType = new FairType();
        fairType.append(rewardFontOptions, Localization.translate("quests", "reward", "reward", builder.toString()));
        fairType.applyParsers(TypeParsers.ItemIcon(rewardFontOptions.getSize(), true, FairItemGlyph::onlyShowNameTooltip));
        return fairType;
    }

    @Override
    public FairType getHandInType(NetworkClient client, boolean outlined) {
        if (this.settlementName == null) {
            return null;
        }
        FontOptions handInFontOptions = new FontOptions(12).outline(outlined);
        FairType fairType = new FairType();
        fairType.append(handInFontOptions, Localization.translate("quests", "handinelder", "settlement", this.settlementName.translate()));
        return fairType;
    }

    @Override
    public boolean canShare() {
        return false;
    }
}

