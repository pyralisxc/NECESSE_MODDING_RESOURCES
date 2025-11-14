/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.mobs.Mob;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public interface HungerMob {
    public float getHungerLevel();

    public void useHunger(float var1, boolean var2);

    public void addHunger(float var1);

    default public boolean useFoodItem(FoodConsumableItem item, boolean giveBuff) {
        if (giveBuff) {
            item.giveFoodBuff((Mob)((Object)this));
        }
        this.addHunger((float)item.nutrition / 100.0f);
        return true;
    }

    default public void submitHungryNotification() {
        if (this instanceof SettlerMob && this.isNoFoodNotificationStillValid()) {
            NetworkSettlementData settlement;
            SettlerMob settlerMob = (SettlerMob)((Object)this);
            SettlementNotificationSeverity severity = SettlementNotificationSeverity.WARNING;
            if (this.getHungerLevel() <= 0.0f) {
                severity = SettlementNotificationSeverity.URGENT;
            }
            if ((settlement = settlerMob.getSettlerSettlementNetworkData()) != null) {
                settlement.notifications.submitNotification("hungry", (SettlerMob)((Object)this), severity);
            }
        }
    }

    default public void removeHungryNotification() {
        SettlerMob settlerMob;
        NetworkSettlementData settlement;
        if (this instanceof SettlerMob && (settlement = (settlerMob = (SettlerMob)((Object)this)).getSettlerSettlementNetworkData()) != null) {
            settlement.notifications.removeNotification("hungry", (SettlerMob)((Object)this));
        }
    }

    default public boolean isNoFoodNotificationStillValid() {
        return this.getHungerLevel() <= 0.15f;
    }
}

