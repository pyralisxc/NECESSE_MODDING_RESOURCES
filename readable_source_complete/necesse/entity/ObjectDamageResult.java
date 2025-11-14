/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity;

import java.util.ArrayList;
import necesse.entity.AbstractDamageResult;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelTile;

public class ObjectDamageResult
extends AbstractDamageResult {
    public final int objectLayerID;

    public ObjectDamageResult(DamagedObjectEntity damagedObjectEntity, LevelTile levelTile, LevelObject levelObject, int objectLayerID, int addedDamage, boolean destroyed, ArrayList<ItemPickupEntity> itemsDropped, boolean showEffects, int mouseX, int mouseY) {
        super(damagedObjectEntity, levelTile, levelObject, addedDamage, destroyed, itemsDropped, showEffects, mouseX, mouseY);
        this.objectLayerID = objectLayerID;
    }
}

