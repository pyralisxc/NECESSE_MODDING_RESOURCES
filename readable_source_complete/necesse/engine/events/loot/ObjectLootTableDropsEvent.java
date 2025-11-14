/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.loot;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.events.GameEvent;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class ObjectLootTableDropsEvent
extends GameEvent {
    public final GameObject object;
    public final Level level;
    public final int layerID;
    public final int tileX;
    public final int tileY;
    public Point dropPos;
    public ArrayList<InventoryItem> objectDrops;
    public ArrayList<InventoryItem> entityDrops;

    public ObjectLootTableDropsEvent(GameObject object, Level level, int layerID, int tileX, int tileY, Point dropPos, ArrayList<InventoryItem> objectDrops, ArrayList<InventoryItem> entityDrops) {
        this.object = object;
        this.level = level;
        this.layerID = layerID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.dropPos = dropPos;
        this.objectDrops = objectDrops;
        this.entityDrops = entityDrops;
    }
}

