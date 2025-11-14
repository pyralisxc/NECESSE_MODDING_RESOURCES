/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.MobSpawnItem;
import necesse.level.maps.Level;

public class ImportedAnimalSpawnItem
extends MobSpawnItem {
    public ImportedAnimalSpawnItem(int stackSize, boolean singleUse, String mobType) {
        super(stackSize, singleUse, mobType);
    }

    @Override
    protected void beforeSpawned(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, Mob mob) {
        if (mob instanceof HusbandryMob) {
            ((HusbandryMob)mob).setImported();
        }
    }
}

