/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.players;

import necesse.engine.events.PreventableGameEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class ItemPlaceEvent
extends PreventableGameEvent {
    public final Level level;
    public final int tileX;
    public final int tileY;
    public final InventoryItem item;
    public final PlayerMob player;

    public ItemPlaceEvent(Level level, int tileX, int tileY, PlayerMob player, InventoryItem item) {
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
        this.item = item;
        this.player = player;
    }
}

