/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.players;

import necesse.engine.events.PreventableGameEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class ObjectInteractEvent
extends PreventableGameEvent {
    public final Level level;
    public final int tileX;
    public final int tileY;
    public final PlayerMob player;

    public ObjectInteractEvent(Level level, int tileX, int tileY, PlayerMob player) {
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
        this.player = player;
    }
}

