/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.expeditions;

import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.util.GameRandom;
import necesse.engine.util.IntRange;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public abstract class SettlerExpedition
implements IDDataContainer {
    public static int minCompleteTicks = 6000;
    public static int maxCompleteTicks = 12000;
    public final IDData idData = new IDData();
    protected GameMessage displayName;
    protected String categoryStringID;
    public static float[] questProgressSuccessChances = new float[]{0.6f, 0.8f, 1.0f};

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public void onExpeditionMissionRegistryClosed() {
    }

    public void initDisplayName() {
        this.displayName = new LocalMessage("expedition", this.getStringID());
    }

    public GameMessage getDisplayName() {
        return this.displayName;
    }

    public GameMessage getFullDisplayName() {
        if (this.categoryStringID == null) {
            return this.getDisplayName();
        }
        GameMessage category = ExpeditionMissionRegistry.categoryDisplayNames.get(this.categoryStringID);
        if (category == null) {
            category = new StaticMessage("NULL");
        }
        return new LocalMessage("ui", "missionnameformat", "name", this.getDisplayName(), "type", category);
    }

    public String getCategoryStringID() {
        return this.categoryStringID;
    }

    public SettlerExpedition setCategory(String categoryStringID) {
        this.categoryStringID = categoryStringID;
        return this;
    }

    public boolean isAvailable(ServerSettlementData settlement) {
        return this.getSuccessChance(settlement) > 0.0f;
    }

    public abstract GameMessage getUnavailableMessage();

    public abstract float getSuccessChance(ServerSettlementData var1);

    public abstract int getBaseCost(ServerSettlementData var1);

    public IntRange getCostRange(int baseCost) {
        if (baseCost == 0) {
            new IntRange(0, 0);
        }
        int minCost = Math.max((int)(0.85f * (float)baseCost), 0);
        int maxCost = Math.max((int)(1.15f * (float)baseCost), 0);
        return new IntRange(minCost, maxCost);
    }

    public final IntRange getCostRange(ServerSettlementData settlement) {
        return this.getCostRange(this.getBaseCost(settlement));
    }

    public int getCurrentCost(ServerSettlementData settlement, long shopSeed) {
        return this.getCostRange(settlement).getRandomValueInRange(new GameRandom(shopSeed).nextSeeded(this.getID()));
    }

    public List<InventoryItem> getItemIcons() {
        return null;
    }

    public abstract List<InventoryItem> getRewardItems(ServerSettlementData var1, HumanMob var2);

    public int getTicksToComplete() {
        return GameRandom.globalRandom.getIntBetween(minCompleteTicks, maxCompleteTicks);
    }

    public static float questProgressSuccessChance(ServerSettlementData settlement, String questTierStringID) {
        return SettlerExpedition.questProgressSuccessChance(settlement, questTierStringID, 0);
    }

    public static float questProgressSuccessChance(ServerSettlementData settlement, String questTierStringID, int offset) {
        int tier = SettlementQuestTier.getTierIndex(questTierStringID);
        int completedTier = settlement.getQuestTiersCompleted();
        int deltaTier = completedTier - tier - offset;
        if (deltaTier < 0) {
            return 0.0f;
        }
        return questProgressSuccessChances[Math.min(deltaTier, questProgressSuccessChances.length - 1)];
    }
}

