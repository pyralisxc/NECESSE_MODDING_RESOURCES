/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import java.awt.geom.Line2D;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class CampfireAddonObjectItem
extends ObjectItem {
    public CampfireAddonObjectItem(GameObject object) {
        super(object);
    }

    @Override
    public boolean onPlaceObject(GameObject object, Level level, int layerID, int tileX, int tileY, int rotation, ServerClient client, InventoryItem item) {
        ObjectEntity oldOE = level.entityManager.getObjectEntity(tileX, tileY);
        boolean success = super.onPlaceObject(object, level, layerID, tileX, tileY, rotation, client, item);
        if (success && oldOE instanceof OEInventory) {
            OEInventory oldInventory = (OEInventory)((Object)oldOE);
            ObjectEntity newOE = level.entityManager.getObjectEntity(tileX, tileY);
            if (newOE instanceof OEInventory) {
                OEInventory newInventory = (OEInventory)((Object)newOE);
                newInventory.getInventory().override(oldInventory.getInventory());
                if (oldOE instanceof FueledInventoryObjectEntity && newOE instanceof FueledInventoryObjectEntity) {
                    ((FueledInventoryObjectEntity)newOE).fuelBurnTime = ((FueledInventoryObjectEntity)oldOE).fuelBurnTime;
                    ((FueledInventoryObjectEntity)newOE).fuelStartTime = ((FueledInventoryObjectEntity)oldOE).fuelStartTime;
                }
            }
        }
        return success;
    }

    @Override
    public boolean canReplace(GameObject object, Level level, int layerID, int tileX, int tileY, int rotation, PlayerMob playerMob, Line2D playerPositionLine, boolean checkRange, InventoryItem item, String error) {
        return false;
    }
}

