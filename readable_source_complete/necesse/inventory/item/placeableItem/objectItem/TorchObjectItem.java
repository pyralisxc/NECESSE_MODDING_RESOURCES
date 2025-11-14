/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.TorchItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class TorchObjectItem
extends ObjectItem
implements TorchItem {
    public TorchObjectItem(GameObject object, boolean dropAsMatDeathPenalty) {
        super(object, dropAsMatDeathPenalty);
    }

    public TorchObjectItem(GameObject object) {
        super(object);
    }

    @Override
    public boolean canPlaceTorch(Level level, int levelX, int levelY, InventoryItem item, PlayerMob player) {
        return this.canPlace(level, levelX, levelY, player, null, item, null) == null;
    }

    @Override
    public int getTorchPlaceRange(Level level, InventoryItem item, PlayerMob player) {
        return this.getPlaceRange(item, player);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("object", "lightsource");
    }
}

