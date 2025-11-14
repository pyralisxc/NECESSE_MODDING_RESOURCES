/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.questItem;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.item.questItem.QuestFishItem;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.desert.DesertBiome;

public class SandRayQuestItem
extends QuestFishItem {
    public SandRayQuestItem() {
        super(new LocalMessage("itemtooltip", "sandrayobtain"));
    }

    @Override
    public FishingLootTable getExtraFishingLoot(ServerClient client, FishingSpot spot) {
        if (spot.getBiome() instanceof DesertBiome && client.playerMob.getInv().getAmount(this, false, false, true, true, "questdrop") <= 0) {
            return new FishingLootTable().addWater(400, this.getStringID());
        }
        return super.getExtraFishingLoot(client, spot);
    }
}

