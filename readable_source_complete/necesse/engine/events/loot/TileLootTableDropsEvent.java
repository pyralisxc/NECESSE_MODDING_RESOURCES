/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.loot;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.events.GameEvent;
import necesse.inventory.InventoryItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class TileLootTableDropsEvent
extends GameEvent {
    public final GameTile tile;
    public final Level level;
    public final int tileX;
    public final int tileY;
    public Point dropPos;
    public ArrayList<InventoryItem> drops;

    public TileLootTableDropsEvent(GameTile tile, Level level, int tileX, int tileY, Point dropPos, ArrayList<InventoryItem> drops) {
        this.tile = tile;
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
        this.dropPos = dropPos;
        this.drops = drops;
    }
}

