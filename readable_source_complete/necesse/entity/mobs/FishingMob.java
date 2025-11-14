/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;

public interface FishingMob {
    public void giveBaitBack(BaitItem var1);

    public void stopFishing();

    public void showFishingWaitAnimation(FishingRodItem var1, int var2, int var3);

    public boolean isFishingSwingDone();

    default public FishingLootTable getFishingLootTable(FishingSpot spot) {
        return spot.getBiome().getFishingLootTable(spot);
    }

    default public void giveCaughtItem(FishingEvent event, InventoryItem item) {
        ItemPickupEntity pickupItem = item.getPickupEntity(event.level, event.getMob().getX(), event.getMob().getY());
        event.level.entityManager.pickups.add(pickupItem);
        pickupItem.pickupCooldown = 0;
    }
}

