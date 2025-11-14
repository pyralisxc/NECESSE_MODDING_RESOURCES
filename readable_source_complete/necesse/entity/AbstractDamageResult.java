/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity;

import java.util.ArrayList;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelTile;

public abstract class AbstractDamageResult {
    public final DamagedObjectEntity damagedObjectEntity;
    public final LevelTile levelTile;
    public final LevelObject levelObject;
    public final int addedDamage;
    public final boolean showEffects;
    public final int mouseX;
    public final int mouseY;
    public final boolean destroyed;
    public final ArrayList<ItemPickupEntity> itemsDropped;

    protected AbstractDamageResult(DamagedObjectEntity damagedObjectEntity, LevelTile levelTile, LevelObject levelObject, int addedDamage, boolean destroyed, ArrayList<ItemPickupEntity> itemsDropped, boolean showEffects, int mouseX, int mouseY) {
        this.damagedObjectEntity = damagedObjectEntity;
        this.levelTile = levelTile;
        this.levelObject = levelObject;
        this.addedDamage = addedDamage;
        this.destroyed = destroyed;
        this.itemsDropped = itemsDropped;
        this.showEffects = showEffects;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public int getTileX() {
        return this.damagedObjectEntity.tileX;
    }

    public int getTileY() {
        return this.damagedObjectEntity.tileY;
    }
}

